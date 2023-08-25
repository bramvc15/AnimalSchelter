package com.spring.schelter;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import perform.PerformeRestAnimal;
import service.SchelterService;
import service.SchelterServiceImpl;
import validator.AddValidation;

@SpringBootApplication
@EnableJpaRepositories("repository")
@EntityScan("domain")
public class ProjectAnimalSchelterApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ProjectAnimalSchelterApplication.class, args);
		
		try {
			new PerformeRestAnimal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/home");
		registry.addViewController("/403").setViewName("403");
	}
	
	@Bean
	AddValidation addValidation() {
		return new AddValidation();
	}
	@Bean
	SchelterService schelterService() {
		return new SchelterServiceImpl();
	}
	@Bean
	SessionLocaleResolver localeResolver() {
	    SessionLocaleResolver slr = new SessionLocaleResolver();
	    slr.setDefaultLocale(Locale.ENGLISH);
	    return slr;
	}


}
