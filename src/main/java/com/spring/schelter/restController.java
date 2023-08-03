package com.spring.schelter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RestController;

import repository.AnimalRepository;

@RestController
public class restController {

	@Autowired
	AnimalRepository animalRepository;
	
	
}
