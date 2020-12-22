package me.dusanov.etl.workshift.etljobapp;

import lombok.RequiredArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.Batch;
import me.dusanov.etl.workshift.etljobapp.model.BatchShiftFailed;
import me.dusanov.etl.workshift.etljobapp.model.Shift;
import me.dusanov.etl.workshift.etljobapp.service.ShiftService;
import me.dusanov.etl.workshift.etljobapp.service.WorkShiftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@RequiredArgsConstructor
public class EtljobappApplication {

	private static final Logger log = LoggerFactory.getLogger(EtljobappApplication.class);

	private final ShiftService shiftService;
	private final WorkShiftClient clientService;

	public static void main(String[] args) {
		SpringApplication.run(EtljobappApplication.class, args);
	}

	@Profile("!test")
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {

			List<ShiftDto> allShiftsFromRestEndpoint = clientService.getAll();
			List<Shift> allShiftsFromLocal = shiftService.getAll();
			List<BatchShiftFailed> allShiftsFromLocalFailed = shiftService.getAllFailed();

			List<Integer> allIds = new ArrayList<>(allShiftsFromRestEndpoint.stream().map(ShiftDto::getId).collect(Collectors.toList()));
			List<Integer> processedIds = new ArrayList<>(allShiftsFromLocal.stream().map(Shift::getId).collect(Collectors.toList()));
			List<Integer> failedIds = new ArrayList<>(allShiftsFromLocalFailed.stream().map(BatchShiftFailed::getShiftId).collect(Collectors.toList()));

			allIds.removeAll(processedIds);
			allIds.removeAll(failedIds);

			if (allIds.size() > 0){

				String ids = allIds.stream().map(id -> (id.toString())).collect(Collectors.joining(","));
				List<ShiftDto> dtos = clientService.getSome(ids);
				Batch batch = new Batch("ART"); //whaaat?
				log.info(String.format("about to save batch %s, with these ids: %s",batch.getId(),ids));
				shiftService.saveBatch(batch,dtos);
				log.info(String.format("batch %s done",batch.getId(),ids));

			}else{
				log.info("nothing to run");
			}
		};
	}

}
