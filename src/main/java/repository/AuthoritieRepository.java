package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import domain.Authoritie;

public interface AuthoritieRepository extends CrudRepository<Authoritie,Long> {
	Optional<String> findByUsername(String username);
}
