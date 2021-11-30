package fi.digitalentconsulting.colors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import fi.digitalentconsulting.colors.entity.Color;
import fi.digitalentconsulting.colors.repository.ColorRepository;
import fi.digitalentconsulting.colors.service.DatamuseService;

@SpringBootApplication
public class BootColorApplication {
	private static Logger LOGGER = LoggerFactory.getLogger(BootColorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BootColorApplication.class, args);
	}

	@Bean
	@Profile("!test")
	public ApplicationRunner testDatamuse(DatamuseService service) {
		return args -> {
			service.getSynonyms("Spring Boot")
				.forEach(word->{
					LOGGER.info("Synonym: {}", word);
				});
				
		};
	}
	
	@Bean
	@Profile("!test")
	public ApplicationRunner initColors(ColorRepository colorRepository) {
		return args -> {
		colorRepository.save(new Color("red", "#ff0000"));
		colorRepository.save(new Color("blue", "#0000ff"));
		colorRepository.save(new Color("fuchsia", "#FF00FF"));
		colorRepository.save(new Color("yellow", "#FFFF00"));		
		colorRepository.save(new Color("white", "#ffffff"));
		colorRepository.save(new Color("black", "#000000"));
		};		
	}
}
