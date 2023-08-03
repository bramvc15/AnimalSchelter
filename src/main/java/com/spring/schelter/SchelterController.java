package com.spring.schelter;

import java.security.Principal;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import domain.Animal;
import domain.User;
import domain.Verblijfplaats;
import jakarta.validation.Valid;
import domain.AdoptedAnimal;
import repository.AdoptedAnimalRepository;
import repository.AnimalRepository;
import repository.GebruikerRepository;
import repository.VerblijfplaatsRepository;
import validator.AddValidation;

@Controller
public class SchelterController {
	@Autowired
	private AnimalRepository animalRepository;
	@Autowired
	private AdoptedAnimalRepository adoptedAnimalRepository;
	@Autowired
	private GebruikerRepository gebruikerRepository;
	@Autowired
	private VerblijfplaatsRepository verblijfplaatsRepository;
	@Autowired
	private AddValidation addValidation;
	@Autowired
	private MessageSource messageSource;

	@GetMapping(value = "/home")
	public String listAnimals(Model model, Principal principal) {

		List<Animal> animalall = (List<Animal>) animalRepository.findAll();
		initOverview(animalall, model, principal);

		return "overview";
	}

	@GetMapping(value = "/filterByBreed/{breed}")
	public String filterByBreed(@PathVariable("breed") String breed, Model model, Principal principal) {

		if (breed != null) {
			Optional<List<Animal>> animalListop = animalRepository.findByBreed(breed);
			if (animalListop.isPresent()) {
				List<Animal> animalall = animalListop.get();
				initOverview(animalall, model, principal);
			}
		} else {
			this.listAnimals(model, principal);
		}
		return "overview";
	}

	public void initOverview(List<Animal> animalall, Model model, Principal principal) {
		String username = principal.getName();
		List<Animal> animalsortede = animalall.stream()
				.sorted(Comparator.comparing(Animal::getBreed).thenComparing(Animal::getDateOfBirth))
				.collect(Collectors.toList());
		model.addAttribute("userName", username);
		model.addAttribute("animalList", animalsortede);
		Optional<List<String>> breedListop = animalRepository.findAllBreeds();
		if (breedListop.isPresent())
			model.addAttribute("breedList", breedListop.get());
	}

	@GetMapping("/animal/{identificatiecode}")
	public String animalDetails(@PathVariable("identificatiecode") String identificatiecode, Model model,
			Principal principal) {
		Optional<Animal> animalop = animalRepository.findById(identificatiecode);

		if (animalop.isPresent()) {

			Animal animal = animalop.get();
			String username = principal.getName();
			LocalDate lt = LocalDate.now();
			int age = lt.compareTo(animal.getDateOfBirth());
			model.addAttribute("userName", username);
			model.addAttribute("animal", animal);
			model.addAttribute("age", age);
			Optional<AdoptedAnimal> reserved = adoptedAnimalRepository.findById(animal.getIdentificatiecode());
			if (reserved.isPresent()) {
				model.addAttribute("adopted", reserved.get().isReserved());
				boolean owner = reserved.get().getUser() != null
						? reserved.get().getUser().getUsername().equals(username) ? true : false
						: false;
				model.addAttribute("owner", owner);

			}
		}

		return "animalDetail";
	}

	@PostMapping(value = "/AddAnimalAdoption/{identificatiecode}")
	public String processReservedAnimal(@PathVariable("identificatiecode") String identificatiecode, Model model,
			Principal principal, RedirectAttributes redirectAttributes, Authentication auth) {
		Optional<Animal> animalop = animalRepository.findById(identificatiecode);
		Optional<AdoptedAnimal> adoptedAnimalop = adoptedAnimalRepository.findById(identificatiecode);

		if (animalop.isPresent() && adoptedAnimalop.isPresent()) {
			Animal animal = animalop.get();
			Optional<User> user = gebruikerRepository.findByUsername(principal.getName());
			AdoptedAnimal adoptedAnimal = adoptedAnimalop.get();
			Optional<List<AdoptedAnimal>> aantaladoptions = adoptedAnimalRepository.findAllByUser(user.get());

			if (aantaladoptions.isPresent() && user.isPresent())
				if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
					processAdoption(animal, adoptedAnimal, user.get());
					redirectAttributes.addFlashAttribute("addMessage", animal.getName() + " was adopted");
				} else if (!user.get().isAsielfan() && aantaladoptions.get().size() < 1) {
					processAdoption(animal, adoptedAnimal, user.get());
					adoptedAnimalRepository.save(adoptedAnimal);
					redirectAttributes.addFlashAttribute("addMessage", animal.getName() + " was adopted");
				} else if (user.get().isAsielfan() && aantaladoptions.get().size() < 2) {
					processAdoption(animal, adoptedAnimal, user.get());
					redirectAttributes.addFlashAttribute("addMessage", animal.getName() + " was adopted");
				} else {
					redirectAttributes.addFlashAttribute("addMessage",
							animal.getName() + " could not be adopted you are over your limit of adoptions");
				}

		}
		return "redirect:/home";
	}

	private void processAdoption(Animal animal, AdoptedAnimal adoptedAnimal, User user) {
		adoptedAnimal.setReserved(true);
		List<Boolean> kanMet = animal.getKanMet();
		kanMet.set(5, true);
		adoptedAnimal.setUser(user);

		animalRepository.save(animal);
		adoptedAnimalRepository.save(adoptedAnimal);
	}

	@PostMapping(value = "/RemoveAnimalAdoption/{identificatiecode}")
	public String processRemoveReservedAnimal(@PathVariable("identificatiecode") String identificatiecode, Model model,
			Principal principal, RedirectAttributes redirectAttributes) {
		Optional<Animal> animalop = animalRepository.findById(identificatiecode);
		Optional<AdoptedAnimal> adoptedAnimalop = adoptedAnimalRepository.findById(identificatiecode);
		if (animalop.isPresent() && adoptedAnimalop.isPresent()) {
			Animal animal = animalop.get();
			AdoptedAnimal adoptedAnimal = adoptedAnimalop.get();
			adoptedAnimal.setReserved(false);
			List<Boolean> kanMet = animal.getKanMet();
			kanMet.set(5, false);
			adoptedAnimal.setUser(null);
			animalRepository.save(animal);
			adoptedAnimalRepository.save(adoptedAnimal);
			redirectAttributes.addFlashAttribute("addMessage", "adoption of " + animal.getName() + " was canceled");
		}

		return "redirect:/home";
	}

	@GetMapping(value = "/addAnimal")
	public String addAnimalForm(Model model, Authentication auth, Principal principal) {
		Animal animal = new Animal();
		animal.setVerplijfplaatsen(
				new ArrayList<>(Arrays.asList(new Verblijfplaats(), new Verblijfplaats(), new Verblijfplaats())));
		model.addAttribute("animal", animal);
		return "addAnimal";
	}

	@PostMapping(value = "/addAnimal")
	public String processAnimalToevoegen(@Valid Animal animal, BindingResult result, Model model, Principal principal,
			RedirectAttributes redirectAttributes, Authentication auth) {
		addValidation.validate(animal, result);
		if (result.hasErrors()) {
			return "addAnimal";
		}

		// Save the animal to the database
		verblijfplaatsRepository.saveAll(animal.getVerplijfplaatsen());
		animalRepository.save(animal);

		redirectAttributes.addFlashAttribute("addMessage", "Animal " + animal.getName() + " was successfully added.");

		return "redirect:/home";
	}

	@GetMapping("/myAdoptedAnimals")
	public String showAdoptedAnimals(Model model, Authentication auth, RedirectAttributes redirectAttributes,
			Principal principal) {
		Optional<List<AdoptedAnimal>> adoptedAnimals;
		String username = principal.getName();
		model.addAttribute("userName", username);
		if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
			// User is an admin, fetch all reserved animals
			Optional<List<String>> adoptedAnimalsop = adoptedAnimalRepository.findAnimalIdsByReservedTrue();
			
			if (!adoptedAnimalsop.isPresent()) {
				redirectAttributes.addFlashAttribute("addMessage", "adoptedAnimalsList is empty");
				
			} else {
				List<Animal> animalList = (List<Animal>) animalRepository.findAllById(adoptedAnimalsop.get());
				model.addAttribute("animalList", animalList);
				return "myAdoptedAnimals";
			}

		} else {
			// User is a regular user, fetch their own reserved animals
			adoptedAnimals = adoptedAnimalRepository.findByUser_UsernameAndReservedTrue(username);
			if (adoptedAnimals.isPresent()) {
				List<String> animalId = adoptedAnimals.get().stream().map(AdoptedAnimal::getAnimalId)
						.collect(Collectors.toList());
				List<Animal> animalList = (List<Animal>) animalRepository.findAllById(animalId);

				model.addAttribute("animalList", animalList);
				return "myAdoptedAnimals";
			}
		}

		return "redirect:/home";
	}

	@PostMapping(value = "/deleteAnimal/{identificatiecode}")
	public String deleteAnimal(@PathVariable("identificatiecode") String identificatiecode, Model model,
			Principal principal, RedirectAttributes redirectAttributes) {
		Optional<Animal> animalop = animalRepository.findById(identificatiecode);
		Optional<AdoptedAnimal> adoptedAnimalop = adoptedAnimalRepository.findById(identificatiecode);

		if (animalop.isPresent() && adoptedAnimalop.isPresent()) {
			Animal animal = animalop.get();
			AdoptedAnimal adoptedAnimal = adoptedAnimalop.get();

			animalRepository.delete(animal);
			adoptedAnimalRepository.delete(adoptedAnimal);
			redirectAttributes.addFlashAttribute("addMessage", animal.getName() + " was successfully deleted.");

		} else {
			redirectAttributes.addFlashAttribute("addMessage", "Animal not found.");
		}
		return "redirect:/home";
	}

}
