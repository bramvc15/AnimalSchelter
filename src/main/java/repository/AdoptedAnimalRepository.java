package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import domain.Authoritie;
import domain.User;
import domain.AdoptedAnimal;
import domain.Animal;

public interface AdoptedAnimalRepository extends CrudRepository<AdoptedAnimal,String> {

	Optional<List<AdoptedAnimal>> findAllByUser(User user);
	Optional<List<AdoptedAnimal>> findAllByReservedFalse();
	@Query("SELECT a.animalId FROM AdoptedAnimal a WHERE a.reserved = true")
	Optional<List<String>> findAnimalIdsByReservedTrue();

	Optional<List<AdoptedAnimal>> findByUser_UsernameAndReservedTrue(String username);
}
