package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import domain.Animal;

public interface AnimalRepository extends CrudRepository<Animal,String> {
	boolean existsByIdentificatiecode(String identificatiecode);
	@Query("SELECT DISTINCT a.breed FROM Animal a")
    Optional<List<String>> findAllBreeds();
	Optional<List<Animal>> findByBreed(String breed);
}
