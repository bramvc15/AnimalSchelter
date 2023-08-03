package validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import domain.Animal;
import repository.AnimalRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class AddValidation implements Validator {

	@Autowired
	private AnimalRepository animalRepository;

	@Autowired
	private MessageSource messageSource;

	@Override
	public boolean supports(Class<?> klass) {
		return Animal.class.isAssignableFrom(klass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Animal animal = (Animal) target;

		// Get the default locale
		Locale defaultLocale = LocaleContextHolder.getLocale();

		// Name validation
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty.animal.name");
		if (!animal.getName().matches("^[a-zA-Z ]+$")) {
			errors.rejectValue("name", "InvalidChars.animal.name");
		}

		// Identification code validation
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "identificatiecode", "NotEmpty.animal.identificatiecode");
		String identificatiecode = animal.getIdentificatiecode();
		if (identificatiecode.length() != 10 || !identificatiecode.substring(0, 3).matches("^[a-zA-Z]+$")
				|| !identificatiecode.substring(3, 10).matches("^[0-9]+$")) {
			errors.rejectValue("identificatiecode", "InvalidFormat.animal.identificatiecode");
		} else {
			int firstFiveDigits = Integer.parseInt(identificatiecode.substring(3, 8));
			int lastTwoDigits = Integer.parseInt(identificatiecode.substring(8));
			if (lastTwoDigits != firstFiveDigits % 3) {
				errors.rejectValue("identificatiecode", "InvalidChecksum.animal.identificatiecode");
			} else if (animalRepository.existsByIdentificatiecode(identificatiecode)) {
				errors.rejectValue("identificatiecode", "Duplicate.animal.identificatiecode");
			}
		}

		// Breed validation
		if (!animal.getBreed().isEmpty() && !animal.getBreed().matches("^[a-zA-Z ]+$")) {
			errors.rejectValue("breed", "InvalidChars.animal.breed");
		}

		// Gender validation
		if (!animal.getGender().equalsIgnoreCase("male") && !animal.getGender().equalsIgnoreCase("female")
				&& !animal.getGender().equalsIgnoreCase("mannetje")
				&& !animal.getGender().equalsIgnoreCase("vrouwtje")) {
			errors.rejectValue("gender", "InvalidValue.animal.gender");
		}

		// Date of birth validation (optional)
		if (animal.getDateOfBirth() == null) {
			String ageEstimation = animal.getAgeEstimation();
			if (!ageEstimation.equalsIgnoreCase("jong") && !ageEstimation.equalsIgnoreCase("volwassen")
					&& !ageEstimation.equalsIgnoreCase("senior") && !ageEstimation.equalsIgnoreCase("young")
					&& !ageEstimation.equalsIgnoreCase("adult")) {
				errors.rejectValue("dateOfBirth", "dateOfBirth was empty and no estimation was selected");
			}
		}else {
			try {
                // Check if the LocalDate is in "DD/MM/YYYY" format
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String dateOfBirthStr = dateFormatter.format(animal.getDateOfBirth());
                LocalDate.parse(dateOfBirthStr, dateFormatter);
            } catch (DateTimeParseException e) {
                errors.rejectValue("dateOfBirth", "InvalidFormat.animal.dateOfBirth");
            }
        }

		// Verblijfplaats validation
		for (int i = 0; i < animal.getVerplijfplaatsen().size(); i++) {
			int hokcode1 = animal.getVerplijfplaatsen().get(i).getHokcode1();
			int hokcode2 = animal.getVerplijfplaatsen().get(i).getHokcode2();
			String hoknaam = animal.getVerplijfplaatsen().get(i).getHoknaam();

			if (hokcode1 < 50 || hokcode1 > 300) {
				errors.rejectValue("verplijfplaatsen[" + i + "].hokcode1", "OutOfRange.animal.hokcode1");
			}
			if (hokcode2 < hokcode1 + 50 || hokcode2 > 300) {
				errors.rejectValue("verplijfplaatsen[" + i + "].hokcode2", "OutOfRange.animal.hokcode2");
			}
			if (!hoknaam.matches("^[a-zA-Z]+$")) {
				errors.rejectValue("verplijfplaatsen[" + i + "].hoknaam", "InvalidChars.animal.hoknaam");
			}
		}
	}

	private String getMessage(String code) {
		return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
	}
}

//@Autowired
//private BoekRepository bookRepository;
//
//@Autowired
//private MessageSource messageSource;
//
//@Override
//public boolean supports(Class<?> klass) {
//  return Boek.class.isAssignableFrom(klass);
//}
//
//@Override
//public void validate(Object target, Errors errors) {
//  Boek boek = (Boek) target;
//
//  // Get the default locale
//  Locale defaultLocale = LocaleContextHolder.getLocale();
//
//  // titel validation
//  String titel = boek.getNaam();
//  if (titel.isBlank() || titel.length() == 0 || titel.isEmpty()) {
//      errors.rejectValue("naam",
//              "lengthOfNaam.boek.naam",
//              getMessage("EmptyNaam"));
//  }
//
//  // isbn validation
//  String isbn = boek.getISBNnummer().toString();
//  if (isbn == null) {
//      errors.rejectValue("ISBNnummer",
//              "lengthOfIsbnNummer.boek.isbnNummer",
//              getMessage("EmptyIsbn"));
//  }
//  if (isbn.length() != 13) {
//      errors.rejectValue("ISBNnummer", "lengthOfIsbn.adminPage.isbn",
//              getMessage("Isbn13"));
//      return;
//  } else {
//      int sum = 0;
//      for (int i = 0; i < 12; i++) {
//          int digit = Character.getNumericValue(isbn.charAt(i));
//          sum += (i % 2 == 0) ? digit : digit * 3;
//      }
//      int checkDigit = (10 - (sum % 10)) % 10;
//      if (checkDigit != Character.getNumericValue(isbn.charAt(12))) {
//          errors.rejectValue("ISBNnummer", "invalidIsbn.adminPage.isbn", getMessage("FaultyIsbn"));
//      }
//  }
//  for (Boek b : bookRepository.findAll()) {
//      if (b.getISBNnummer().equals(isbn))
//          errors.rejectValue("ISBNnummer", "notUniqueISBN.adminPage.isbn", getMessage("UniqueIsbn"));
//  }
//
//  // prijs validation
//  Double prijs = boek.getAankoopPrijs();
//  if (prijs != null) {
//      if (prijs <= 0 || prijs > 100) {
//          errors.rejectValue("aankoopPrijs",
//                  "lengthOfPrijs.boek.prijs",
//                  getMessage("InvalidPrice"));
//      }
//  }
//
//  // auteur validation
//  List<Auteur> auteurs = boek.getAuteurs();
//  Auteur auteur = auteurs.get(0);
//  if (auteur == null || auteur.getAuteurNaam().isEmpty()) {
//      errors.rejectValue("auteurs[0].auteurNaam",
//              "lengthOfAuteurs.boek.auteurs",
//              getMessage("NoAuthor"));
//  }
//
//  // locatie validation
//  List<Locatie> locaties = boek.getLocaties();
//  Locatie locatie1 = locaties.get(0);
//  if (locatie1 == null || locatie1.getPlaatscode1().isEmpty() ||
//          locatie1.getPlaatscode2().isEmpty() || locatie1.getPlaatsnaam().isEmpty()) {
//      errors.rejectValue("locaties[0].plaatscode1",
//              "lengthOfLocaties.boek.locaties",
//              getMessage("NoLocation"));
//  }
//  for (Locatie loc : boek.getLocaties()) {
//      if (loc != null && !loc.getPlaatscode1().isBlank() &&
//              !loc.getPlaatscode2().isBlank() && !loc.getPlaatsnaam().isBlank()) {
//          int locCode1 = Integer.parseInt(loc.getPlaatscode1());
//          int locCode2 = Integer.parseInt(loc.getPlaatscode2());
//          if (locCode1 < 50 || locCode1 > 300 || locCode2 < 50 || locCode2 > 300) {
//              errors.rejectValue("locaties[0].plaatscode1",
//                      "invalidLocatie.boek.locaties",
//                      getMessage("PlacecodeOFB"));
//          }
//          if (locCode1 - locCode2 < 50) {
//              errors.rejectValue("locaties[0].plaatscode1",
//                      "invalidLocatie.boek.locaties",
//                      getMessage("PlacecodeDiff"));
//          }
//          if (!loc.getPlaatsnaam().matches("[a-zA-Z]+")) {
//              errors.rejectValue("locaties[0].plaatscode1",
//                      "invalidPlaatsNaam.boek.locaties",
//                      getMessage("PlacenameOL"));
//          }
//      }
//  }
//}
//
//private String getMessage(String code) {
//  return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
//}
