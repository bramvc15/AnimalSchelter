package repository;


import java.util.Optional;

import org.springframework.data.repository.CrudRepository;


import domain.User;

public interface GebruikerRepository extends CrudRepository<User, Integer> {

	Optional<User> findByUsername(String name);
}
