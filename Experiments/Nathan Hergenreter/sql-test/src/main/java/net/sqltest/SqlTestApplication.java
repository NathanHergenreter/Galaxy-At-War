package net.sqltest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import net.sqltest.sql_stuff.Country;
import net.sqltest.sql_stuff.CountryRepository;

//SpringBootApplication tag indicates main springboot method
@SpringBootApplication
public class SqlTestApplication {
	
	//private static final Logger log = LoggerFactory.getLogger(SqlTestApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SqlTestApplication.class, args);
	}
	
	/* "A bean is an object that is instantiated, assembled, and otherwise 
	 *   managed by a Spring IoC container."
	 *  
	 * IoC (Inversion of Control) is a process in which an object defines its 
	 *   dependencies without creating them. This object delegates the job of 
	 *   constructing such dependencies to an IoC container
	*/
	/*
	@Bean
	public CommandLineRunner demo(CountryRepository repository) {
		return (args) -> {
			//Adds countries to database
			repository.save(new Country("India"));
			repository.save(new Country("Brazil"));
			repository.save(new Country("USA"));
			repository.save(new Country("Italy"));
			repository.save(new Country("China"));
			repository.save(new Country("Germany"));
			repository.save(new Country("Iran"));

			//Fetches all countries
			log.info("Countries found with findAll():");
			log.info("-------------------------------");
			for (Country country : repository.findAll()) {
				log.info(country.toString());
			}
			log.info("");

			//Fetch an individual country by ID
			repository.findById(1L)
				.ifPresent(country -> {
					log.info("Country found with findById(1L):");
					log.info("--------------------------------");
					log.info(country.toString());
					log.info("");
				});

			//Fetch countries by name
			log.info("Country found with findByName('Germany'):");
			log.info("--------------------------------------------");
			repository.findByName("Germany").forEach(country -> {
				log.info(country.toString());
			});
			// for (Country germany : repository.findByName("Germany")) {
			// 	log.info(germany.toString());
			// }
			log.info("");
		};
	}
	*/
}

