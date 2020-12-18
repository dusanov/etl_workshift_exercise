package me.dusanov.etl.workshift.etljobapp;

import lombok.Getter;
import lombok.Setter;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@ConfigurationProperties(prefix = "workshift.endpoint")
@SpringBootApplication
public class EtljobappApplication {

	private static final Logger log = LoggerFactory.getLogger(EtljobappApplication.class);

	@Getter @Setter	private String url;

	public static void main(String[] args) {
		SpringApplication.run(EtljobappApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Profile("!test")
	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {

			log.info("args passed: " + Arrays.toString(args));

			ShiftDto dto = restTemplate.getForObject(
					url+"/1", ShiftDto.class);

			log.info("dto: " + String.valueOf(dto));
		};
	}

}
