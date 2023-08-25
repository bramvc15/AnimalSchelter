package validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
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
		if (animal.getDateOfBirth().compareTo(LocalDate.now()) > 0) {
			errors.rejectValue("dateOfBirth", "dateWasInTheFuture");
		}
		if (animal.getDateOfBirth() == null) {
			String ageEstimation = animal.getAgeEstimation();
			if (!ageEstimation.equalsIgnoreCase("Jong") && !ageEstimation.equalsIgnoreCase("Volwassen")
					&& !ageEstimation.equalsIgnoreCase("Senior") && !ageEstimation.equalsIgnoreCase("Young")
					&& !ageEstimation.equalsIgnoreCase("Adult")) {
				errors.rejectValue("dateOfBirth", "dateOfBirthWasEmptyAndNoEstimationSelected");
			}
		} else {
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
			System.out.println(hokcode1 + hokcode2 + hoknaam);
			if (hokcode1 == 0 && hokcode2 == 0 && hoknaam.equals("")) {
			
			}
			else {
				if (hokcode1 < 50 || hokcode1 > 300) {
					String errorMessage = messageSource.getMessage("OutOfRange.animal.hokcode1",
							new Object[] { 50, 300 }, Locale.ENGLISH);
					errors.rejectValue("verplijfplaatsen[" + i + "].hokcode1", "outOfRange", errorMessage);
				}
				if (hokcode2 > 300) {
					String errorMessage = messageSource.getMessage("OutOfRange.animal.hokcode2",
							new Object[] { 50, 300 }, Locale.ENGLISH);
					errors.rejectValue("verplijfplaatsen[" + i + "].hokcode2", "outOfRange", errorMessage);
				}
				if (hokcode2 < hokcode1 + 50 ) {
					String errorMessage = messageSource.getMessage("Not.biggger.enough.hokcode2",
							new Object[] { 50}, Locale.ENGLISH);
					errors.rejectValue("verplijfplaatsen[" + i + "].hokcode2", "outOfRange", errorMessage);
				}
				if (!hoknaam.matches("^[a-zA-Z]+$")) {
					errors.rejectValue("verplijfplaatsen[" + i + "].hoknaam", "InvalidChars.animal.hoknaam");
				}
			}
		}

	}

	private String getMessage(String code) {
		return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
	}
}
