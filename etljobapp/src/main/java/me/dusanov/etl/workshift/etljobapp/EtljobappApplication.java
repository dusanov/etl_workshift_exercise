package me.dusanov.etl.workshift.etljobapp;

import lombok.Getter;
import lombok.Setter;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.Batch;
import me.dusanov.etl.workshift.etljobapp.service.ShiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@ConfigurationProperties(prefix = "workshift.endpoint")
@SpringBootApplication
public class EtljobappApplication {

	private static final Logger log = LoggerFactory.getLogger(EtljobappApplication.class);

	@Getter @Setter	private String url;
	@Autowired	ShiftService shiftService;

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

			Batch batch = new Batch("EST");
			//no time for this mambo jumbo,
			// if there is an argument it has to be shiftId
			// if not, all shifts will be called
			if (args.length == 1){
				if (args[0].contains(",")){
					try {
						ShiftDto[] dtos = restTemplate.getForObject(url+"?ids="+args[0], ShiftDto[].class);
						for (ShiftDto dto : dtos) shiftService.saveShift(dto,batch);
						log.info("done for: " + args[0]);
					} catch (Exception e){
						log.error("there was an error: ", e);
						System.exit(-1);
					}
				}
				else{
					try {
						ShiftDto dto = restTemplate.getForObject(url+"/" + args[0], ShiftDto.class);
						shiftService.saveShift(dto,batch);
						log.info("done for: " + args[0]);
					} catch (Exception e){
						log.error("there was an error: ", e);
						System.exit(-1);
					}
				}
			}
			else{
				int err = 0;
				try {
					ShiftDto[] dtos = restTemplate.getForObject(url, ShiftDto[].class);
					for (ShiftDto dto : dtos) shiftService.saveShift(dto,batch);
					log.info("done for get all shifts ");
				} catch (Exception e){
					log.error("there was an error: ", e);
					err = -1;
				}
				System.exit(err);

			}

		};
	}

}
