package me.dusanov.etl.workshift.etljobapp;

import lombok.RequiredArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.etl.WorkShiftJob;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@RequiredArgsConstructor
public class EtljobappApplication {

	private final WorkShiftJob workShiftJob;

	public static void main(String[] args) {
		SpringApplication.run(EtljobappApplication.class, args);
	}

	@Profile("!test")
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
			workShiftJob.execute();
			System.exit(0);
		};
	}

}
