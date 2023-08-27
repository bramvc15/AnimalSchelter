package com.spring.schelter;

import java.security.Principal;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import domain.Animal;
import domain.User;
import domain.Verblijfplaats;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import domain.AdoptedAnimal;
import repository.AdoptedAnimalRepository;
import repository.AnimalRepository;
import repository.GebruikerRepository;
import repository.VerblijfplaatsRepository;
import utility.Message;
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
	public String listAnimals(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "lang", required = false) String lang, Model model, Principal principal) {
		List<Animal> animalall = (List<Animal>) animalRepository.findAll();
		initOverview(animalall, model, principal);
		if (lang != null) {
			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
			if (localeResolver != null) {
				localeResolver.setLocale(request, response, new Locale(lang));
			}
		}
		return "overview";
	}

	@GetMapping(value = "/filterByBreed")
	public String filterByBreed(@RequestParam(name = "selectedBreed") String breed, Model model, Principal principal) {

		if (breed != null) {
			Optional<List<Animal>> animalListop = animalRepository.findByBreed(breed);
			if (animalListop.isPresent()) {
				List<Animal> animalall = animalListop.get();
				initOverview(animalall, model, principal);
				model.addAttribute("selectedBreed", breed);
			}
		} else {
			List<Animal> animalall = (List<Animal>) animalRepository.findAll();
			initOverview(animalall, model, principal);
		}

		return "overview";
	}

	private void initOverview(List<Animal> animalall, Model model, Principal principal) {
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
			if(animal.getDateOfBirth()!= null) {
				int age = lt.compareTo(animal.getDateOfBirth());
				model.addAttribute("age", age);
			}
			else {
				String age = animal.getAgeEstimation();
				model.addAttribute("age", age);
			}
			model.addAttribute("userName", username);
			model.addAttribute("animal", animal);
			
			Optional<AdoptedAnimal> reserved = adoptedAnimalRepository
					.findByAnimal_Identificatiecode(animal.getIdentificatiecode());
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
			Principal principal, RedirectAttributes redirectAttributes, Authentication auth, Locale locale) {
		Optional<Animal> animalop = animalRepository.findById(identificatiecode);
		Optional<AdoptedAnimal> adoptedAnimalop = adoptedAnimalRepository
				.findByAnimal_Identificatiecode(identificatiecode);

		if (animalop.isPresent() && adoptedAnimalop.isPresent()) {
			Animal animal = animalop.get();
			Optional<User> user = gebruikerRepository.findByUsername(principal.getName());
			AdoptedAnimal adoptedAnimal = adoptedAnimalop.get();

			if (user.isPresent()) {
				Optional<List<AdoptedAnimal>> aantaladoptions = adoptedAnimalRepository.findByUser(user.get());
				int currentAdoptions = aantaladoptions.get().size();
				if (aantaladoptions.isPresent()) {
					if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
						processAdoption(animal, adoptedAnimal, user.get());
						redirectAttributes.addFlashAttribute("addMessage", messageSource.getMessage("animal.was.adopted", new Object[]{animal.getName(), currentAdoptions+1}, locale));
						
					} else if (!user.get().isAsielfan() && currentAdoptions < 1) {
						processAdoption(animal, adoptedAnimal, user.get());
						adoptedAnimalRepository.save(adoptedAnimal);
						redirectAttributes.addFlashAttribute("addMessage", messageSource.getMessage("animal.was.adopted", new Object[]{animal.getName(), currentAdoptions+1}, locale));
					} else if (user.get().isAsielfan() && currentAdoptions < 2) {
						processAdoption(animal, adoptedAnimal, user.get());
						redirectAttributes.addFlashAttribute("addMessage", messageSource.getMessage("animal.was.adopted", new Object[]{animal.getName(), currentAdoptions+1}, locale));
					} else {
						redirectAttributes.addFlashAttribute("addMessage", messageSource.getMessage("over.adoption.limit", new Object[]{animal.getName(),currentAdoptions}, locale));
					}
				}
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
			Principal principal, RedirectAttributes redirectAttributes, Locale locale) {
		Optional<Animal> animalop = animalRepository.findById(identificatiecode);
		Optional<AdoptedAnimal> adoptedAnimalop = adoptedAnimalRepository
				.findByAnimal_Identificatiecode(identificatiecode);
		if (animalop.isPresent() && adoptedAnimalop.isPresent()) {
			Animal animal = animalop.get();
			AdoptedAnimal adoptedAnimal = adoptedAnimalop.get();
			adoptedAnimal.setReserved(false);
			List<Boolean> kanMet = animal.getKanMet();
			kanMet.set(5, false);
			adoptedAnimal.setUser(null);
			animalRepository.save(animal);
			adoptedAnimalRepository.save(adoptedAnimal);
			redirectAttributes.addFlashAttribute("addMessage", messageSource.getMessage("animal.adoption.canceled", new Object[]{animal.getName()}, locale));
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

		Optional<User> user = gebruikerRepository.findByUsername(principal.getName());
		List<Verblijfplaats> verblijfplaatsList = new ArrayList<>();
		addValidation.validate(animal, result);

		if (result.hasErrors()) {
			return "addAnimal";
		}

		for (Verblijfplaats verblijfplaats : animal.getVerplijfplaatsen()) {
			if (verblijfplaats.getHokcode1() != 0 && verblijfplaats.getHokcode2() != 0
					&& verblijfplaats.getHoknaam() != null) {
				Verblijfplaats ver = new Verblijfplaats(verblijfplaats.getHokcode1(), verblijfplaats.getHokcode2(),
						verblijfplaats.getHoknaam());
				verblijfplaatsRepository.save(ver);
				verblijfplaatsList.add(ver);
			}
		}
		animal.setVerplijfplaatsen(verblijfplaatsList);
		animal.setName(animal.getName().substring(0,1).toUpperCase() + animal.getName().substring(1));
		animalRepository.save(animal);
		if (user.isPresent()) {
			AdoptedAnimal adoptedAnimal = new AdoptedAnimal(animal, false, user.get());
			adoptedAnimalRepository.save(adoptedAnimal);
		}
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
			Optional<List<Animal>> adoptedAnimalsop = adoptedAnimalRepository.findAnimalsByReservedTrue();

			if (!adoptedAnimalsop.isPresent()) {
				redirectAttributes.addFlashAttribute("addMessage", "adoptedAnimalsList is empty");

			} else {
				model.addAttribute("animalList", adoptedAnimalsop.get());
				return "myAdoptedAnimalsOverview";
			}

		} else {
			// User is a regular user, fetch their own reserved animals
			adoptedAnimals = adoptedAnimalRepository.findByUser_UsernameAndReservedTrue(username);
			if (adoptedAnimals.isPresent()) {
				List<Animal> animalList = adoptedAnimals.get().stream().map(AdoptedAnimal::getAnimal)
						.collect(Collectors.toList());

				model.addAttribute("animalList", animalList);
				return "myAdoptedAnimalsOverview";
			}
		}

		return "redirect:/home";
	}

	@PostMapping(value = "/deleteAnimal/{identificatiecode}")
	public String deleteAnimal(@PathVariable("identificatiecode") String identificatiecode, Model model,
			Principal principal, RedirectAttributes redirectAttributes) {
		Optional<Animal> animalop = animalRepository.findById(identificatiecode);
		Optional<AdoptedAnimal> adoptedAnimalop = adoptedAnimalRepository
				.findByAnimal_Identificatiecode(identificatiecode);

		if (animalop.isPresent() && adoptedAnimalop.isPresent()) {
			Animal animal = animalop.get();
			AdoptedAnimal adoptedAnimal = adoptedAnimalop.get();
			adoptedAnimalRepository.delete(adoptedAnimal);
			animalRepository.delete(animal);
			
			redirectAttributes.addFlashAttribute("addMessage", animal.getName() + " was successfully deleted.");

		} else {
			redirectAttributes.addFlashAttribute("addMessage", "Animal not found.");
		}
		return "redirect:/home";
	}

}
