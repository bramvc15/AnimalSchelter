package com.spring.schelter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.spring.schelter.RestController;

import domain.AdoptedAnimal;
import domain.Animal;
import domain.User;
import domain.Verblijfplaats;
import repository.AdoptedAnimalRepository;
import repository.AnimalRepository;
import repository.GebruikerRepository;
import repository.VerblijfplaatsRepository;
import validator.AddValidation;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class AnimalMockTest {

	@Autowired
    private MockMvc mockMvc;

    @Mock
    private AnimalRepository mockAnimalRepository;

    @Mock
    private AdoptedAnimalRepository mockAdoptedAnimalRepository;

    @Mock
    private GebruikerRepository mockGebruikerRepository;

    @Mock
    private VerblijfplaatsRepository mockVerblijfplaatsRepository;

    @InjectMocks
    private SchelterController controller;
    @Mock
    private AddValidation addValidationMock;
    @Mock
	private MessageSource messageSource;
    
    
    Principal principal;

    @Captor
    private ArgumentCaptor<List<Verblijfplaats>> captor;

	private Animal animal1;
	private Animal animal2;

	List<Verblijfplaats> verblijfplaatsList = List.of(new Verblijfplaats(67, 150, "LE"),
			new Verblijfplaats(148, 225, "OU"), new Verblijfplaats(63, 135, "TI"), new Verblijfplaats(51, 154, "KJ"),
			new Verblijfplaats(77, 239, "AT"), new Verblijfplaats(113, 262, "UR"), new Verblijfplaats(78, 282, "YU"),
			new Verblijfplaats(88, 209, "RF"), new Verblijfplaats(184, 258, "ED"), new Verblijfplaats(95, 179, "NM"),
			new Verblijfplaats(196, 283, "VT"), new Verblijfplaats(101, 160, "PL"), new Verblijfplaats(94, 271, "OX"),
			new Verblijfplaats(132, 193, "VU"), new Verblijfplaats(171, 296, "MA"), new Verblijfplaats(66, 123, "ZA"),
			new Verblijfplaats(81, 246, "HG"), new Verblijfplaats(79, 203, "IW"), new Verblijfplaats(146, 277, "BN"),
			new Verblijfplaats(138, 230, "EF"));

	@BeforeEach
	public void before() {
		MockitoAnnotations.openMocks(this);
        mockMvc = standaloneSetup(controller).build();
        ReflectionTestUtils.setField(controller, "animalRepository", mockAnimalRepository);
        ReflectionTestUtils.setField(controller, "adoptedAnimalRepository", mockAdoptedAnimalRepository);
        ReflectionTestUtils.setField(controller, "gebruikerRepository", mockGebruikerRepository);
        ReflectionTestUtils.setField(controller, "verblijfplaatsRepository", mockVerblijfplaatsRepository);
        principal = new UsernamePasswordAuthenticationToken("user", "password");
        addValidationMock = mock(AddValidation.class);
		animal1 = new Animal("KEB6487301", "Zeus",
				"https://static.wixstatic.com/media/53899f_8b0ef8fb5d994b19b1ff55428f61852d~mv2.jpg/v1/fill/w_1000,h_667,al_c,q_85,usm_0.66_1.00_0.01/53899f_8b0ef8fb5d994b19b1ff55428f61852d~mv2.jpg",
				"German shepherd", "male", LocalDate.of(2015, 04, 20), 120,
				List.of(verblijfplaatsList.get(11), verblijfplaatsList.get(12)),
				new ArrayList<>(List.of(false, true, false, true, false, false)));
		animal2 = new Animal("KEB4879500", "Kelly",
				"https://www.thesprucepets.com/thmb/zXkzVVV5P8h2JG0ZUFtXtvIq-lM=/3600x0/filters:no_upscale():strip_icc()/bulldog-4584344-hero-8b60f1e867f046e792ba092eec669256.jpg",
				"Bulldog", "female", LocalDate.of(2018, 03, 01), 80,
				List.of(verblijfplaatsList.get(6), verblijfplaatsList.get(7)),
				new ArrayList<>(List.of(true, false, true, true, false, false)));
	}

	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testAccessWithUserRole() throws Exception {
		// Create a Principal object with the desired username
		

		mockMvc.perform(get("/home").principal(principal)).andExpect(status().isOk()).andExpect(view().name("overview"))
				.andExpect(model().attributeExists("userName")).andExpect(model().attribute("userName", "user"));
	}

	// test if overview page gives a list of animals
	@WithMockUser(username = "user", roles = { "USER", "ADMIN" })
	@Test
	public void testListAnimals() throws Exception {
		List<Animal> animals = new ArrayList<>();
		animals.add(animal1);
		animals.add(animal2);

		
		when(mockAnimalRepository.findAll()).thenReturn(animals);
		mockMvc.perform(get("/home").principal(principal)).andExpect(status().isOk()).andExpect(view().name("overview"))
				.andExpect(model().attributeExists("animalList"));

		verify(mockAnimalRepository).findAll();
	}

	// test for filtering on breed option
	@Test
	public void testFilterByBreed() throws Exception {
	    String breed = "German shepherd";
	    List<Animal> animals = new ArrayList<>();
	    
	    // Create a sample animal for testing
	    Animal sampleAnimal = new Animal();
	    sampleAnimal.setBreed(breed);
	    animals.add(sampleAnimal);

	    when(mockAnimalRepository.findByBreed(breed)).thenReturn(Optional.of(animals));

	    mockMvc.perform(get("/filterByBreed")
	            .param("selectedBreed", breed) // Pass the breed parameter
	            .principal(principal))
	            .andExpect(status().isOk())
	            .andExpect(view().name("overview"))
	            .andExpect(model().attributeExists("animalList"))
	            .andExpect(model().attribute("selectedBreed", breed)); // Add assertion for selectedBreed attribute

	    verify(mockAnimalRepository).findByBreed(breed);
	    // You can add more verifications based on your test case requirements
	}
	// test for animal details
    @Test
    public void testAnimalDetails() throws Exception {
        Animal animal = animal1;
        animal.setIdentificatiecode("AKR4879500");
        
        when(mockAnimalRepository.findById(animal.getIdentificatiecode())).thenReturn(Optional.of(animal));
        when(mockAdoptedAnimalRepository.findByAnimal_Identificatiecode(animal.getIdentificatiecode())).thenReturn(Optional.empty());

        mockMvc.perform(get("/animal/{identificatiecode}", animal.getIdentificatiecode()).principal(principal))
            .andExpect(status().isOk())
            .andExpect(view().name("animalDetail"))
            .andExpect(model().attribute("animal", animal));
        verify(mockAnimalRepository).findById(animal.getIdentificatiecode());
        verify(mockAdoptedAnimalRepository).findByAnimal_Identificatiecode(animal.getIdentificatiecode());
    }

	// Test adding of adoption on detail page
    @Test
    public void testProcessReservedAnimal_isOk() throws Exception {
        Animal animal = animal1;
        animal.setIdentificatiecode("KEB6548700");

        User user = new User(0, "user", "password", true, true);
        AdoptedAnimal adoptedAnimal = new AdoptedAnimal(animal, false, user);
        List<AdoptedAnimal> adoptedAnimalList = List.of(new AdoptedAnimal(animal, false, user));

        when(mockGebruikerRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(mockAnimalRepository.findById(animal.getIdentificatiecode())).thenReturn(Optional.of(animal));
        when(mockAdoptedAnimalRepository.findByAnimal_Identificatiecode(animal.getIdentificatiecode())).thenReturn(Optional.of(adoptedAnimal));
        when(mockAdoptedAnimalRepository.findByUser(user)).thenReturn(Optional.of(adoptedAnimalList));
        when(messageSource.getMessage(eq("animal.was.adopted"), any(Object[].class), eq(Locale.ENGLISH)))
            .thenReturn("Zeus was adopted (current adoptions = 1)");

        mockMvc.perform(post("/AddAnimalAdoption/{identificatiecode}", animal.getIdentificatiecode()).principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andExpect(flash().attribute("addMessage", containsString(animal.getName() + " was adopted (current adoptions = 1)")));

        // Verify the interactions
        verify(mockAnimalRepository).findById(animal.getIdentificatiecode());
        verify(mockAdoptedAnimalRepository).findByAnimal_Identificatiecode(animal.getIdentificatiecode());
        verify(mockAdoptedAnimalRepository).findByUser(user);
        verify(mockAdoptedAnimalRepository).save(any(AdoptedAnimal.class));
        verify(messageSource).getMessage(eq("animal.was.adopted"), any(Object[].class), eq(Locale.ENGLISH));
    }

    //test to many adoptions 
    @Test
    public void testProcessReservedAnimal_ToManyAdoptions() throws Exception {
        Animal animal = animal1;
        animal.setIdentificatiecode("KEB4879500");
        
        // Create a user with too many adoptions
        User user = new User(0, "user", "password", false, true);
        AdoptedAnimal adoptedAnimal = new AdoptedAnimal(animal, false, user);
        List<AdoptedAnimal> adoptedAnimalList = List.of(new AdoptedAnimal(animal, false, user));
        
        when(mockGebruikerRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(mockAnimalRepository.findById(animal.getIdentificatiecode())).thenReturn(Optional.of(animal));
        when(mockAdoptedAnimalRepository.findByAnimal_Identificatiecode(animal.getIdentificatiecode())).thenReturn(Optional.of(adoptedAnimal));
        when(mockAdoptedAnimalRepository.findByUser(user)).thenReturn(Optional.of(adoptedAnimalList));
        when(messageSource.getMessage(eq("over.adoption.limit"), any(Object[].class), eq(Locale.ENGLISH)))
            .thenReturn("Zeus could not be adopted you are over your limit of adoptions (current adoptions = 1)");
        
        mockMvc.perform(post("/AddAnimalAdoption/{identificatiecode}", animal.getIdentificatiecode()).principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andExpect(flash().attribute("addMessage", animal.getName() +  " could not be adopted you are over your limit of adoptions (current adoptions = 1)"));

        // Verify the interactions
        verify(mockGebruikerRepository).findByUsername("user");
        verify(mockAnimalRepository).findById(animal.getIdentificatiecode());
        verify(mockAdoptedAnimalRepository).findByAnimal_Identificatiecode(animal.getIdentificatiecode());
        verify(mockAdoptedAnimalRepository).findByUser(user);
        verify(messageSource).getMessage(eq("over.adoption.limit"), any(Object[].class), eq(Locale.ENGLISH));
    }



	// Test Removing of an adoption on detail page
    @Test
    public void testProcessRemoveReservedAnimal_isOk() throws Exception {
        Animal animal = animal1;
        animal.setIdentificatiecode("KEB4879500");

        User user = new User(0, "user", "password", true, true);
        AdoptedAnimal adoptedAnimal = new AdoptedAnimal(animal, true, user);
        List<AdoptedAnimal> adoptedAnimalList = List.of(adoptedAnimal);
        animal.setKanMet(new ArrayList<>(List.of(false, true, false, true, false, true)));

        when(mockAnimalRepository.findById(animal.getIdentificatiecode())).thenReturn(Optional.of(animal));
        when(mockAdoptedAnimalRepository.findByAnimal_Identificatiecode(animal.getIdentificatiecode())).thenReturn(Optional.of(adoptedAnimal));
        when(messageSource.getMessage("animal.adoption.canceled", new Object[]{animal.getName(),0},Locale.ENGLISH))
        .thenReturn("Adoption for {0} was canceled");

        mockMvc.perform(post("/RemoveAnimalAdoption/{identificatiecode}", animal.getIdentificatiecode()).principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(mockAnimalRepository).findById(animal.getIdentificatiecode());
        verify(mockAdoptedAnimalRepository).findByAnimal_Identificatiecode(animal.getIdentificatiecode());
        verify(mockAnimalRepository).save(any(Animal.class));
        verify(mockAdoptedAnimalRepository).save(any(AdoptedAnimal.class));
    }
    
	// Test actualy adding an animal
    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testProcessAnimalToevoegen() throws Exception {
        Animal animalToAdd = new Animal("KEB5872549", "Kelly",
				"https://www.thesprucepets.com/thmb/zXkzVVV5P8h2JG0ZUFtXtvIq-lM=/3600x0/filters:no_upscale():strip_icc()/bulldog-4584344-hero-8b60f1e867f046e792ba092eec669256.jpg",
				"Bulldog", "female", LocalDate.of(2018, 03, 01), 80, List.of(verblijfplaatsList.get(5)),
				new ArrayList<>(List.of(true, false, false, true, false, false)));
        
        // Mock validation result
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        // Mock repositories
        when(mockAnimalRepository.save(any(Animal.class))).thenReturn(animalToAdd);
        
        mockMvc.perform(post("/addAnimal")
                .principal(principal)
                .flashAttr("animal", animalToAdd)
                .param("param1", "value1") // Add request parameters as needed
                .param("param2", "value2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andExpect(flash().attribute("addMessage", "Animal " + animalToAdd.getName() + " was successfully added."));

        verify(mockAnimalRepository).save(any(Animal.class));
        // You can add more verifications based on your test case requirements
    }
	// Test Show all adopted animals from the user or if admin all adopted animals
    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testShowAdoptedAnimalsUser() throws Exception {
    	
        List<AdoptedAnimal> adoptedAnimals = List.of(new AdoptedAnimal(animal1, false, null), 
        		new AdoptedAnimal(animal2, false, null));

        when(mockAdoptedAnimalRepository.findByUser_UsernameAndReservedTrue("user"))
                .thenReturn(Optional.of(adoptedAnimals));

        mockMvc.perform(get("/myAdoptedAnimals").principal(principal))
        .andExpect(status().isOk())
        .andExpect(view().name("myAdoptedAnimalsOverview"))
        .andExpect(model().attributeExists("animalList"))
        .andReturn();


        verify(mockAdoptedAnimalRepository).findByUser_UsernameAndReservedTrue("user");
    }

    
	// Test deleting an animal
    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testDeleteAnimal() throws Exception {
        String identificatiecode = "KEB6597189";
        User user = new User(0, "user", "password", true, true);
        Animal animalToDelete = new Animal(identificatiecode, "zeus", "image-url", "Breed", "male", LocalDate.now(), 100, Collections.emptyList(), Collections.emptyList());
        AdoptedAnimal adoptedAnimalToDelete = new AdoptedAnimal(animalToDelete, false, user);

        when(mockAnimalRepository.findById(identificatiecode)).thenReturn(Optional.of(animalToDelete));
        when(mockAdoptedAnimalRepository.findByAnimal_Identificatiecode(identificatiecode)).thenReturn(Optional.of(adoptedAnimalToDelete));

        mockMvc.perform(post("/deleteAnimal/{identificatiecode}", identificatiecode)
                .principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andExpect(flash().attribute("addMessage", containsString(animalToDelete.getName() + " was successfully deleted.")));

        verify(mockAnimalRepository).delete(animalToDelete);
        verify(mockAdoptedAnimalRepository).delete(adoptedAnimalToDelete);
    }
    

}
