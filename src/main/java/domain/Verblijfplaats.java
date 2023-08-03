package domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Verblijfplaats implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private int hokcode1;
	private int hokcode2;
	private String hoknaam;
	private String bewooner;
	
	public Verblijfplaats(int hokcode1, int hokcode2, String hoknaam) {
		this.hokcode1 = hokcode1;
		this.hokcode2 = hokcode2;
		this.hoknaam = hoknaam;
	}
}
