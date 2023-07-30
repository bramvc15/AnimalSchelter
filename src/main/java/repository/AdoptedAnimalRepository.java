package repository;

import org.springframework.data.repository.CrudRepository;

import domain.Authoritie;
import domain.AdoptedAnimal;

public interface AdoptedAnimalRepository extends CrudRepository<AdoptedAnimal,String> {

}
