package perform;

import java.util.List;

import org.springframework.web.reactive.function.client.WebClient;

import domain.Animal;
import reactor.core.publisher.Mono;

public class PerformeRestAnimal {
	private final String SERVER_URI = "http://localhost:8080/rest";
	private WebClient webClient = WebClient.create();

	public PerformeRestAnimal() {
		System.out.println("------FIND ALL Bulldogs -------");
		giveAllAnimalsOfBreed("Bulldog");
		System.out.println("------FIND ALL Male Animals -------");
		giveAllAnimalsOfGender("male");
		System.out.println("------FIND ALL Non Reserved Animals -------");
		giveAllNonReservedAnimals();
	}

	public void giveAllAnimalsOfBreed(String breed) {
		webClient.get().uri(SERVER_URI + "/breed/" + breed).retrieve().bodyToFlux(Animal.class).flatMap(animal -> {
			printAnimalData(animal);
			return Mono.empty();
		}).blockLast();
	}
	public void giveAllAnimalsOfGender(String gender) {
		webClient.get().uri(SERVER_URI + "/gender/" + gender).retrieve().bodyToFlux(Animal.class).flatMap(animal -> {
			printAnimalData(animal);
			return Mono.empty();
		}).blockLast();
	}
	public void giveAllNonReservedAnimals() {
		webClient.get().uri(SERVER_URI + "/notReserved").retrieve().bodyToFlux(Animal.class).flatMap(animal -> {
			printAnimalData(animal);
			return Mono.empty();
		}).blockLast();
	}
	public void printAnimalData(Animal animal) {
		System.out.println(animal.toString());
		
	}
}
