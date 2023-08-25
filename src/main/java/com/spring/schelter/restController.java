package com.spring.schelter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import domain.AdoptedAnimal;
import domain.Animal;
import repository.AdoptedAnimalRepository;
import repository.AnimalRepository;

@org.springframework.web.bind.annotation.RestController
@RequestMapping(value="/rest")
public class RestController {

	@Autowired
	AnimalRepository animalRepository;
	@Autowired
	AdoptedAnimalRepository adoptedAnimalRepository;
	
	@GetMapping(value = "/breed/{breed}")
	public List<Animal> giveAllAnimalsOfBreed(@PathVariable String breed) {
		return animalRepository.findByBreed(breed).get();
	}
	@GetMapping(value="/gender/{gender}")
	public List<Animal> giveAllAnimalsOfGender(@PathVariable String gender){
		return animalRepository.findByGender(gender);
	}
	@GetMapping(value="/notReserved")
	public List<Animal> giveAllNonReservedAnimals(){
		return adoptedAnimalRepository.findAnimalsByReservedFalse().get(); 
	}
	
	
}
