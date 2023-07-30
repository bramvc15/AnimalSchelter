package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AdoptedAnimal implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String animalId;
	private boolean reserved;
	@ManyToOne
	private User user;
	
	public AdoptedAnimal(String animalId, boolean reserved, User userName) {
		this.animalId = animalId;
		this.reserved = reserved;
		this.user = userName;
	}

	
	
}
