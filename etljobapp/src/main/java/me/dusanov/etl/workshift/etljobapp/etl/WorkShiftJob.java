package me.dusanov.etl.workshift.etljobapp.etl;

import lombok.RequiredArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.EtljobappApplication;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.Batch;
import me.dusanov.etl.workshift.etljobapp.model.BatchShiftFailed;
import me.dusanov.etl.workshift.etljobapp.model.Shift;
import me.dusanov.etl.workshift.etljobapp.service.WorkShiftClient;
import me.dusanov.etl.workshift.etljobapp.service.WorkShiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WorkShiftJob implements IEtlJob {

    private static final Logger log = LoggerFactory.getLogger(EtljobappApplication.class);

    private final WorkShiftService workShiftService;
    private final WorkShiftClient clientService;

    @Override
    public void execute() {

        Batch batch = workShiftService.createNewBatch(new Batch());

        List<Integer> processedIds = workShiftService.getAll()
                .stream()
                .map(Shift::getId)
                .collect(Collectors.toList());

        List<Integer> failedIds = workShiftService.getAllFailed()
                .stream()
                .map(BatchShiftFailed::getShiftId)
                .collect(Collectors.toList());

        List<Integer> allIds = clientService.getAll(batch)
                .stream()
                .filter(shiftDto -> !processedIds.contains(shiftDto.getId()) && !failedIds.contains(shiftDto.getId()))
                .map(ShiftDto::getId)
                .collect(Collectors.toList());

        if (allIds.size() > 0){

            String ids = allIds.stream().map(Object::toString).collect(Collectors.joining(","));
            List<ShiftDto> dtos = clientService.getSome(batch, ids);
            log.info(String.format("about to create a batch for shift ids: %s",ids));
            workShiftService.executeBatch(batch, dtos);
            log.info(String.format("batch %s done for shift ids: %s",batch.getId(),ids));

        }else{
            log.info("nothing to run");
        }
    }
}
