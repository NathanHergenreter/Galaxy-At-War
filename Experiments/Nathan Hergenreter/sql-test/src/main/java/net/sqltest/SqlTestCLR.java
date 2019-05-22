package net.sqltest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.sqltest.sql_stuff.Country;
import net.sqltest.sql_stuff.CountryRepository;

@Component
public class SqlTestCLR implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SqlTestApplication.class);

	@Autowired
	private CountryRepository repository;

	 @Override
	 public void run(String...args) {
			//Adds countries to database
		 	Country india = new Country("India");
		 	Country nepal = new Country("Nepal");
		 	india.addFriend(nepal);
		 	//test.neighbors.add(new Country("Pakistan"));
		 	//test.neighbors.add(new Country("Bangladesh"));
			repository.save(india);
			repository.save(nepal);
			/*
			repository.findByName("India").forEach(country -> {
				log.info(country.toString());
				log.info(repository.findByName("Nepal").get(0).toString());
				country.addFriend(repository.findByName("Nepal").get(0));
			});
			*/
			repository.findByName("India").forEach(country -> {
				country.setName("Hindustan");
				//repository.updateName((int) country.getId(), "Hindustan");
				log.info(country.toString());
				//country.addFriend(repository.findByName("Nepal").get(0));
				repository.save(country);
			});
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
	 }
}
