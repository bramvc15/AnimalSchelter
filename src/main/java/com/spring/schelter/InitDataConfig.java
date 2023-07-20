package com.spring.schelter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import domain.Animal;
import domain.Verblijfplaats;
import repository.AnimalRepository;
import repository.VerblijfplaatsRepository;

@Component
public class InitDataConfig implements CommandLineRunner {
	@Autowired
	private AnimalRepository animalRepository;

	@Autowired
	private VerblijfplaatsRepository verblijfplaatsRepository;
	@Override
	public void run(String... args) {

		List<Verblijfplaats> verblijfplaatsList = List.of(

				new Verblijfplaats(67, 150, "LE"),
				new Verblijfplaats(148, 225, "OU"),
				new Verblijfplaats(63, 135, "TI"),
				new Verblijfplaats(51, 154, "KJ"),
				new Verblijfplaats(77, 239, "AT"),
				new Verblijfplaats(113, 262, "UR"),
				new Verblijfplaats(78, 282, "YU"),
				new Verblijfplaats(88, 209, "RF"),
				new Verblijfplaats(184, 258, "ED"),
				new Verblijfplaats(95, 179, "NM"),
				new Verblijfplaats(196, 283, "VT"),
				new Verblijfplaats(101, 160, "PL"),
				new Verblijfplaats(94, 271, "OX"),
				new Verblijfplaats(132, 193, "VU"),
				new Verblijfplaats(171, 296, "MA"),
				new Verblijfplaats(66, 123, "ZA"),
				new Verblijfplaats(81, 246, "HG"),
				new Verblijfplaats(79, 203, "IW"),
				new Verblijfplaats(146, 277, "BN"),
				new Verblijfplaats(138, 230, "EF")


		);
		List<Animal> animalList = List.of(

				new Animal("KEB2154785", "bento",
						"https://images.unsplash.com/photo-1611003228941-98852ba62227?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YmFieSUyMGRvZ3xlbnwwfHwwfHx8MA%3D%3D&w=1000&q=80",
						"Golden retriever", "M", LocalDateTime.now(), 150.22, List.of(verblijfplaatsList.get(1),verblijfplaatsList.get(2),verblijfplaatsList.get(0))),
				new Animal("KEB65789214", "marie",
						"https://www.litter-robot.com/media/magefan_blog/david-brooke-ragdoll_copy.jpg",
						"Ragdoll", "F", LocalDateTime.now(), 150.22, List.of(verblijfplaatsList.get(3),verblijfplaatsList.get(4)))

		);
		verblijfplaatsRepository.saveAll(verblijfplaatsList);
		animalRepository.saveAll(animalList);
	}

}
