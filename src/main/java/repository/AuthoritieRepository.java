package repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import domain.Authoritie;

public interface AuthoritieRepository extends CrudRepository<Authoritie,Long> {
	String findByUsername(String username);
}
