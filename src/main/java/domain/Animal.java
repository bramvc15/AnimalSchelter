package domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Animal implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String identificatiecode;
	private String name;
	private String img;
	private String breed;
	private String geslacht;
	private LocalDateTime geboortedatum;
	private double medischeKosten;
	private List<Verblijfplaats> verplijfplaatsen;
	
	public Animal(String identificatiecode, String naam, String img, String ras, String geslacht,LocalDateTime geboortedatum, double medischeKosten, List<Verblijfplaats> ver) {
		this.identificatiecode= identificatiecode;
		this.name = naam;
		this.img = img;
		this.breed = ras;
		this.geslacht = geslacht;
		this.geboortedatum = geboortedatum;
		this.medischeKosten = medischeKosten;
		this.verplijfplaatsen = ver;
	}
}
