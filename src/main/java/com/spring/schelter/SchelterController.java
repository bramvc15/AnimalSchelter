package com.spring.schelter;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import repository.AnimalRepository;

@Controller
public class SchelterController {
	@Autowired
	private AnimalRepository animalRepository;
	
	@GetMapping(value= "/home")
	public String listAnimals(Model model, Principal principal) {
		model.addAttribute("animalList", animalRepository.findAll());
		return "overview";
	}
}
