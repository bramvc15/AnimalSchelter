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
	@Query("SELECT a.animal FROM AdoptedAnimal a WHERE a.reserved = false")
	Optional<List<Animal>> findAnimalsByReservedFalse();
	@Query("SELECT a.animal FROM AdoptedAnimal a WHERE a.reserved = true")
	Optional<List<Animal>> findAnimalsByReservedTrue();

	Optional<List<AdoptedAnimal>> findByUser_UsernameAndReservedTrue(String username);
	Optional<AdoptedAnimal> findByAnimal_Identificatiecode(String identificatiecode);
	Optional<List<AdoptedAnimal>> findByUser(User user);
}
