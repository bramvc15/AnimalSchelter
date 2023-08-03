package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.NumberFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "verplijfplaatsen")
public class Animal implements Serializable {
    private static final long serialVersionUID = 1L;


//    @NotBlank(message="{NotEmpty.animal.identificatiecode}")
    @Id
    private String identificatiecode;

//    @NotBlank(message="{NotEmpty.animal.name}")
    @JsonProperty("animal_name")
    private String name;

    private String img;
    private String breed;
    private String gender;
    private LocalDate dateOfBirth;
    private String ageEstimation;
//    @NotNull
    private double medicalCost;

    @OneToMany
    private List<Verblijfplaats> verplijfplaatsen;

    private List<Boolean> kanMet = new ArrayList<>(List.of(false, false, false, false, false, false));


    public String getFormattedMedicalCost() {
        // Format the medicalCost to two decimal places
        return String.format("%.2f", medicalCost);
    }

    public Animal(String identificatiecode, String name, String img, String breed, String gender,
            LocalDate dateOfBirth, double medicalCost, List<Verblijfplaats> verplijfplaatsen,
            List<Boolean> kanMet) {
        this.identificatiecode = identificatiecode;
        this.name = name;
        this.img = img;
        this.breed = breed;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.medicalCost = medicalCost;
        this.verplijfplaatsen = verplijfplaatsen;
        this.kanMet = kanMet;

        for (Verblijfplaats verblijfplaats : verplijfplaatsen) {
            verblijfplaats.setBewooner(this.identificatiecode);
        }
    }
}
