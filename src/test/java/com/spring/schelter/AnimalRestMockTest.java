package com.spring.schelter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.spring.schelter.RestController;

import domain.Animal;
import domain.Verblijfplaats;
import repository.AdoptedAnimalRepository;
import repository.AnimalRepository;

@SpringBootTest
class AnimalRestMockTest {

	@Mock
	private AnimalRepository mock;
	@Mock
	private AdoptedAnimalRepository mockAdoptedAnimalRepository;
	private RestController controller;
	private MockMvc mockMvc;
	private Animal anim;
	@BeforeEach
	public void before() {
		MockitoAnnotations.openMocks(this);
		controller = new RestController();
		mockMvc = standaloneSetup(controller).build();
		ReflectionTestUtils.setField(controller, "animalRepository", mock);
		ReflectionTestUtils.setField(controller, "adoptedAnimalRepository", mockAdoptedAnimalRepository);
		anim = new Animal(identificatiecode,name,img,breed,gender,
	            dateOfBirth, medicalCost, vebrlijfplaatsen,
	            kanMet);
	}
	List<Verblijfplaats> verblijfplaatsList = List.of(

			new Verblijfplaats(67, 150, "LE"), new Verblijfplaats(148, 225, "OU"),
			new Verblijfplaats(63, 135, "TI"), new Verblijfplaats(51, 154, "KJ"), new Verblijfplaats(77, 239, "AT"),
			new Verblijfplaats(113, 262, "UR"), new Verblijfplaats(78, 282, "YU"),
			new Verblijfplaats(88, 209, "RF"), new Verblijfplaats(184, 258, "ED"),
			new Verblijfplaats(95, 179, "NM"), new Verblijfplaats(196, 283, "VT"),
			new Verblijfplaats(101, 160, "PL"), new Verblijfplaats(94, 271, "OX"),
			new Verblijfplaats(132, 193, "VU"), new Verblijfplaats(171, 296, "MA"),
			new Verblijfplaats(66, 123, "ZA"), new Verblijfplaats(81, 246, "HG"), new Verblijfplaats(79, 203, "IW"),
			new Verblijfplaats(146, 277, "BN"), new Verblijfplaats(138, 230, "EF")

			
			
	);
	
	String identificatiecode = "KEB2154785";
	String name = "Bento";
	String img = "https://images.unsplash.com/photo-1611003228941-98852ba62227?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YmFieSUyMGRvZ3xlbnwwfHwwfHx8MA%3D%3D&w=1000&q=80";
	String breed = "Golden retriever";
	String gender ="male";
	double medicalCost = 150.22;
	LocalDate dateOfBirth = LocalDate.of(2018, 5, 6);
	List<Verblijfplaats> vebrlijfplaatsen = List.of(verblijfplaatsList.get(1), verblijfplaatsList.get(2), verblijfplaatsList.get(0));
	private List<Boolean> kanMet = List.of(true, true, true, false, false, false);

	
	

	// testGetAnimalByBreed_isOK()
	private void performRest(String uri) throws Exception {
		mockMvc.perform(get(uri))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].identificatiecode").value(identificatiecode))
        .andExpect(jsonPath("$[0].animal_name").value(name))
        .andExpect(jsonPath("$[0].img").value(img))
        .andExpect(jsonPath("$[0].breed").value(breed))
        .andExpect(jsonPath("$[0].gender").value(gender))
        .andExpect(jsonPath("$[0].medicalCost").value(medicalCost))
        .andExpect(jsonPath("$[0].verplijfplaatsen").isArray())
        .andExpect(jsonPath("$[0].verplijfplaatsen.length()").value(vebrlijfplaatsen.size()))
        .andExpect(jsonPath("$[0].kanMet").isArray())
        .andExpect(jsonPath("$[0].kanMet.length()").value(kanMet.size()))
        .andExpect(jsonPath("$[0].kanMet[0]").value(kanMet.get(0)))
        .andExpect(jsonPath("$[0].kanMet[1]").value(kanMet.get(1)))
        .andExpect(jsonPath("$[0].kanMet[2]").value(kanMet.get(2)))
        .andExpect(jsonPath("$[0].kanMet[3]").value(kanMet.get(3)))
        .andExpect(jsonPath("$[0].kanMet[4]").value(kanMet.get(4)))
        .andExpect(jsonPath("$[0].kanMet[5]").value(kanMet.get(5)));
		
	}
	@Test
	public void testGetAnimalByBreed_isOK() throws Exception {
	    String breedtest = "Golden retriever";
	    List<Animal> animalList = List.of(this.anim);
	    Mockito.when(mock.findByBreed(breedtest)).thenReturn(Optional.of(animalList));
	    performRest("/rest/breed/" + breedtest);
	    Mockito.verify(mock).findByBreed(breedtest);
	}
	@Test
	public void testGetAnimalByBreed_emptyList() throws Exception {
	    String breedtest = "Nonexistent Breed";
	    List<Animal> emptyAnimalList = Collections.emptyList();
	    Mockito.when(mock.findByBreed(breedtest)).thenReturn(Optional.of(emptyAnimalList));
	    
	    mockMvc.perform(get("/rest/breed/" + breedtest))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$").isEmpty());
	    
	    Mockito.verify(mock).findByBreed(breedtest);
	}
	@Test
	public void testGetByGender_isOk() throws Exception {
	    String gender = "male";
	    
	    Animal animal1 = new Animal("KEB6597189", "zeus",
				"https://static.wixstatic.com/media/53899f_8b0ef8fb5d994b19b1ff55428f61852d~mv2.jpg/v1/fill/w_1000,h_667,al_c,q_85,usm_0.66_1.00_0.01/53899f_8b0ef8fb5d994b19b1ff55428f61852d~mv2.jpg",
				"German shepherd", "male", LocalDate.of(2015, 04, 20), 120,
				List.of(verblijfplaatsList.get(11), verblijfplaatsList.get(12)),
				List.of(false, true, false, true, false, false));
	    Animal animal2 = new Animal("KEB5872549", "Kelly",
				"https://www.thesprucepets.com/thmb/zXkzVVV5P8h2JG0ZUFtXtvIq-lM=/3600x0/filters:no_upscale():strip_icc()/bulldog-4584344-hero-8b60f1e867f046e792ba092eec669256.jpg",
				"Bulldog", "female", LocalDate.of(2018, 03, 01), 80,
				List.of(verblijfplaatsList.get(6), verblijfplaatsList.get(7)),
				List.of(true, false, true, true, false, false));
	    
	    List<Animal> animalList = List.of(animal1, animal2);
	    
	    Mockito.when(mock.findByGender(gender)).thenReturn(animalList);
	    
	    mockMvc.perform(get("/rest/gender/" + gender))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$").isArray())
	            .andExpect(jsonPath("$.length()").value(2)); // 2 because there is also 1 created in the before each
	    
	    Mockito.verify(mock).findByGender(gender);
	}

	@Test
	public void testGetNonReservedAnimals_isOk() throws Exception {
		Animal animal1 = new Animal("KEB6597189", "zeus",
				"https://static.wixstatic.com/media/53899f_8b0ef8fb5d994b19b1ff55428f61852d~mv2.jpg/v1/fill/w_1000,h_667,al_c,q_85,usm_0.66_1.00_0.01/53899f_8b0ef8fb5d994b19b1ff55428f61852d~mv2.jpg",
				"German shepherd", "male", LocalDate.of(2015, 04, 20), 120,
				List.of(verblijfplaatsList.get(11), verblijfplaatsList.get(12)),
				List.of(false, true, false, true, false, false));
	    Animal animal2 = new Animal("KEB5872549", "Kelly",
				"https://www.thesprucepets.com/thmb/zXkzVVV5P8h2JG0ZUFtXtvIq-lM=/3600x0/filters:no_upscale():strip_icc()/bulldog-4584344-hero-8b60f1e867f046e792ba092eec669256.jpg",
				"Bulldog", "female", LocalDate.of(2018, 03, 01), 80,
				List.of(verblijfplaatsList.get(6), verblijfplaatsList.get(7)),
				List.of(true, false, true, true, false, false));
	    
	    List<Animal> animalList = List.of(animal1, animal2);

	    Mockito.when(mockAdoptedAnimalRepository.findAnimalsByReservedFalse()).thenReturn(Optional.of(animalList));

	    mockMvc.perform(get("/rest/notReserved"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$").isArray())
	            .andExpect(jsonPath("$.length()").value(2));

	    Mockito.verify(mockAdoptedAnimalRepository).findAnimalsByReservedFalse();
	}

	


}
