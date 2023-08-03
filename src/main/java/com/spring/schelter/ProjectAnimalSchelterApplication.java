package com.spring.schelter;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import service.SchelterService;
import service.SchelterServiceImpl;
import validator.AddValidation;

@SpringBootApplication
@EnableJpaRepositories("repository")
@EntityScan("domain")
public class ProjectAnimalSchelterApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ProjectAnimalSchelterApplication.class, args);
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

}
