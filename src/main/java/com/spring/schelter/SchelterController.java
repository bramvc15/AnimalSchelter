package com.spring.schelter;

import java.security.Principal;
import java.time.*;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import domain.Animal;
import domain.Authoritie;
import domain.User;
import domain.AdoptedAnimal;
import repository.AdoptedAnimalRepository;
import repository.AnimalRepository;
import repository.GebruikerRepository;

@Controller
public class SchelterController {
	@Autowired
	private AnimalRepository animalRepository;
	@Autowired
	private AdoptedAnimalRepository adoptedAnimalRepository;
	@Autowired
	private GebruikerRepository gebruikerRepository;

	@GetMapping(value = "/home")
	public String listAnimals(Model model, Principal principal) {
		String username = principal.getName();
		model.addAttribute("userName", username);
		model.addAttribute("animalList", animalRepository.findAll());
		return "overview";
	}

	@GetMapping("/animal/{identificatiecode}")
	public String animalDetails(@PathVariable("identificatiecode") String identificatiecode, Model model,
			Principal principal) {
		Optional<Animal> animalop = animalRepository.findById(identificatiecode);

		if (!animalop.isEmpty()) {

			Animal animal = animalop.get();
			String username = principal.getName();
			LocalDate lt = LocalDate.now();
			int age = lt.compareTo(animal.getDateOfBirth());
			model.addAttribute("userName", username);
			model.addAttribute("animal", animal);
			model.addAttribute("age", age);
			Optional<AdoptedAnimal> reserved = adoptedAnimalRepository.findById(animal.getIdentificatiecode());
			if (!reserved.isEmpty()) {
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
			Principal principal, RedirectAttributes redirectAttributes) {
		Optional<Animal> animalop = animalRepository.findById(identificatiecode);
		Optional<AdoptedAnimal> adoptedAnimalop = adoptedAnimalRepository.findById(identificatiecode);
		if (!animalop.isEmpty() && !adoptedAnimalop.isEmpty()) {
			Animal animal = animalop.get();
			AdoptedAnimal adoptedAnimal = adoptedAnimalop.get();
			adoptedAnimal.setReserved(true);
			animal.setAdopted(true);
			User user = gebruikerRepository.findByUsername(principal.getName());

			adoptedAnimal.setUser(user);
			animalRepository.save(animal);
			adoptedAnimalRepository.save(adoptedAnimal);

			redirectAttributes.addFlashAttribute("addMessage", animal.getName() + " was adopted");

		}
		return "redirect:/home";
	}

	@PostMapping(value = "/RemoveAnimalAdoption/{identificatiecode}")
	public String processRemoveReservedAnimal(@PathVariable("identificatiecode") String identificatiecode, Model model,
			Principal principal, RedirectAttributes redirectAttributes) {
		Optional<Animal> animalop = animalRepository.findById(identificatiecode);
		Optional<AdoptedAnimal> adoptedAnimalop = adoptedAnimalRepository.findById(identificatiecode);
		if (!animalop.isEmpty() && !adoptedAnimalop.isEmpty()) {
			Animal animal = animalop.get();
			AdoptedAnimal adoptedAnimal = adoptedAnimalop.get();
			adoptedAnimal.setReserved(false);

			animal.setAdopted(false);
			adoptedAnimal.setUser(null);
			animalRepository.save(animal);
			adoptedAnimalRepository.save(adoptedAnimal);
			redirectAttributes.addFlashAttribute("addMessage", "adoption of " + animal.getName() + " was canceled");
		}

		return "redirect:/home";
	}
}
