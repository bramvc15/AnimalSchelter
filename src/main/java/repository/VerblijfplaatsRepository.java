package repository;

import org.springframework.data.repository.CrudRepository;

import domain.Verblijfplaats;

public interface VerblijfplaatsRepository extends CrudRepository<Verblijfplaats,Long> {
	boolean existsByHokcode1(int hokcode1);
	boolean existsByHokcode2(int hokcode2);
	boolean existsByHoknaam(String hoknaam);
}
