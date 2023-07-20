package service;

import org.springframework.beans.factory.annotation.Autowired;

import repository.AnimalRepository;

public class SchelterServiceImpl implements SchelterService {
	@Autowired
	private AnimalRepository animalRepository;
}
