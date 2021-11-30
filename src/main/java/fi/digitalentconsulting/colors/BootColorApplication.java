package fi.digitalentconsulting.colors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import fi.digitalentconsulting.colors.service.DatamuseService;

@SpringBootApplication
public class BootColorApplication {
	private static Logger LOGGER = LoggerFactory.getLogger(BootColorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BootColorApplication.class, args);
	}

	@Bean
	public ApplicationRunner testDatamuse(DatamuseService service) {
		return args -> {
			service.getSynonyms("Spring")
				.forEach(word->{
					LOGGER.info("Synonym: {}", word);
				});
				
		};
	}
}
