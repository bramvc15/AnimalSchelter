package domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity 
@Table(name = "Authorities")
@Getter @Setter
@EqualsAndHashCode(exclude = "id")
@ToString(exclude="id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authoritie implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String username;
	private String authority;
	public Authoritie(String userName, String role) {
		this.username = userName;
		this.authority = role;
	}
}
