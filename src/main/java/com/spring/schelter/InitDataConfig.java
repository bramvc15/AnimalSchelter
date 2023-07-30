package com.spring.schelter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import domain.Animal;
import domain.Authoritie;
import domain.AdoptedAnimal;
import domain.User;
import domain.Verblijfplaats;
import repository.AnimalRepository;
import repository.AuthoritieRepository;
import repository.GebruikerRepository;
import repository.AdoptedAnimalRepository;
import repository.VerblijfplaatsRepository;

@Component
public class InitDataConfig implements CommandLineRunner {
	@Autowired
	private AnimalRepository animalRepository;

	@Autowired
	private VerblijfplaatsRepository verblijfplaatsRepository;

	@Autowired
	private GebruikerRepository gebruikerRepository;

	@Autowired
	private AuthoritieRepository authoritieRepository;

	@Autowired
	private AdoptedAnimalRepository reservedAnimalRepository;

	@Override
	public void run(String... args) {

		List<Verblijfplaats> verblijfplaatsList = List.of(

				new Verblijfplaats(67, 150, "LE"), new Verblijfplaats(148, 225, "OU"),
				new Verblijfplaats(63, 135, "TI"), new Verblijfplaats(51, 154, "KJ"), new Verblijfplaats(77, 239, "AT"),
				new Verblijfplaats(113, 262, "UR"), new Verblijfplaats(78, 282, "YU"),
				new Verblijfplaats(88, 209, "RF"), new Verblijfplaats(184, 258, "ED"),
				new Verblijfplaats(95, 179, "NM"), new Verblijfplaats(196, 283, "VT"),
				new Verblijfplaats(101, 160, "PL"), new Verblijfplaats(94, 271, "OX"),
				new Verblijfplaats(132, 193, "VU"), new Verblijfplaats(171, 296, "MA"),
				new Verblijfplaats(66, 123, "ZA"), new Verblijfplaats(81, 246, "HG"), new Verblijfplaats(79, 203, "IW"),
				new Verblijfplaats(146, 277, "BN"), new Verblijfplaats(138, 230, "EF")

		);
		List<Animal> animalList = List.of(
				new Animal("KEB2154785", "Bento",
						"https://images.unsplash.com/photo-1611003228941-98852ba62227?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YmFieSUyMGRvZ3xlbnwwfHwwfHx8MA%3D%3D&w=1000&q=80",
						"Golden retriever", "M", LocalDate.of(2018, 5, 6), 150.22,
						List.of(verblijfplaatsList.get(1), verblijfplaatsList.get(2), verblijfplaatsList.get(0))),
				new Animal("KEB6578921", "Marie",
						"https://images.ctfassets.net/440y9b545yd9/4PjMuZHJH4FawjAelQ0oHk/14c0f2950f6b7a65e656c4e5fd4b639c/Ragdoll850.jpg",
						"Ragdoll", "F", LocalDate.of(2017, 2, 13), 50,
						List.of(verblijfplaatsList.get(3), verblijfplaatsList.get(4))),
				new Animal("KEB6685489", "Lizzy",
						"https://www.dierenasielgent.be/assets/img/gallery/362299265_652784683548253_6844719345018827865_n-20230724113644-r-q90-m1690191403.jpg",
						"European shorthair", "F", LocalDate.of(2020, 01, 01), 10, List.of(verblijfplaatsList.get(5))),
				new Animal("KEB5872549", "Kelly",
						"https://www.thesprucepets.com/thmb/zXkzVVV5P8h2JG0ZUFtXtvIq-lM=/3600x0/filters:no_upscale():strip_icc()/bulldog-4584344-hero-8b60f1e867f046e792ba092eec669256.jpg",
						"Bulldog", "F", LocalDate.of(2018, 03, 01), 80,
						List.of(verblijfplaatsList.get(6), verblijfplaatsList.get(7))),
				new Animal("KEB9878729", "Milo",
						"https://upload.wikimedia.org/wikipedia/commons/e/ef/GraceTheGreyhound.jpg", "greyhound", "F",
						LocalDate.of(2018, 03, 01), 80, List.of(verblijfplaatsList.get(8), verblijfplaatsList.get(9))),
				new Animal("KEB6871285", "Bella",
						"https://cdn.shopify.com/s/files/1/0009/1566/9028/files/chihuahua-heart-ID-tag_large.jpg?v=1547569168",
						"Golden retriever", "M", LocalDate.of(2016, 3, 18), 75.99, List.of(verblijfplaatsList.get(10))),
				new Animal("KEB6741867", "Mr Whiskers",
						"https://greekreporter.com/wp-content/uploads/2021/09/aegean-cat-greece-breed-credit-anna-wichmann-greek-reporter.jpg",
						"Aegean", "M", LocalDate.of(2020, 5, 6), 30, List.of(verblijfplaatsList.get(13))),
				new Animal("KEB6597189", "zeus",
						"https://static.wixstatic.com/media/53899f_8b0ef8fb5d994b19b1ff55428f61852d~mv2.jpg/v1/fill/w_1000,h_667,al_c,q_85,usm_0.66_1.00_0.01/53899f_8b0ef8fb5d994b19b1ff55428f61852d~mv2.jpg",
						"European shorthair", "M", LocalDate.of(2015, 04, 20), 120,
						List.of(verblijfplaatsList.get(11), verblijfplaatsList.get(12))),
				new Animal("KEB6297489", "kato",
						"https://cdn.shopify.com/s/files/1/1199/8502/files/persian-doll-face.jpg", "Bulldog", "F",
						LocalDate.of(2018, 03, 01), 80, List.of(verblijfplaatsList.get(14))),
				new Animal("KEB6878873", "Lucy",
						"https://animals.net/wp-content/uploads/2018/09/Mini-Bull-Terrier-4-650x425.jpg",
						"mini bull terrier zwart", "F", LocalDate.of(2019, 04, 30), 80,
						List.of(verblijfplaatsList.get(15), verblijfplaatsList.get(16)))

		);
		
		User g1 = new User("admin", "$2y$10$xT2EKeAP.Ey84iy5dOwuOe5hxtRhvGVk6aLIpgAIpAzzu8xfJWPpO", true);
		User g2 = new User("bram", "$2y$10$E.442wS/c9QXLpkLcXaOY.Bet9jTm/aoOUi65yvtuvmJuBJJu1KcG", true);
		Authoritie a1 = new Authoritie("admin", "ROLE_ADMIN");
		Authoritie a2 = new Authoritie("bram", "ROLE_USER");
		List<AdoptedAnimal> reserved = List.of(new AdoptedAnimal("KEB2154785", true, g2),
				new AdoptedAnimal("KEB6578921", false, null), new AdoptedAnimal("KEB6685489", false, null),
				new AdoptedAnimal("KEB5872549", false, g2), new AdoptedAnimal("KEB9878729", true, g2),
				new AdoptedAnimal("KEB6871285", false, null), new AdoptedAnimal("KEB6741867", false, null),
				new AdoptedAnimal("KEB6597189", true, g2), new AdoptedAnimal("KEB6297489", true, g2),
				new AdoptedAnimal("KEB6878873", false, null));
		
		verblijfplaatsRepository.saveAll(verblijfplaatsList);
		animalRepository.saveAll(animalList);

		gebruikerRepository.save(g1);
		gebruikerRepository.save(g2);
		authoritieRepository.save(a1);
		authoritieRepository.save(a2);
		reservedAnimalRepository.saveAll(reserved);
	}

}
