package repository;

import org.springframework.data.repository.CrudRepository;

import domain.Animal;

public interface AnimalRepository extends CrudRepository<Animal,String> {


}
