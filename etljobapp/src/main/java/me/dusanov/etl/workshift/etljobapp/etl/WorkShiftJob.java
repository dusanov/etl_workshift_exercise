package me.dusanov.etl.workshift.etljobapp.etl;

import lombok.RequiredArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.EtljobappApplication;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.Batch;
import me.dusanov.etl.workshift.etljobapp.model.BatchShiftFailed;
import me.dusanov.etl.workshift.etljobapp.model.Shift;
import me.dusanov.etl.workshift.etljobapp.service.WorkShiftService;
import me.dusanov.etl.workshift.etljobapp.service.WorkShiftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

        List<ShiftDto> allShiftsFromRestEndpoint = clientService.getAll();
        List<Shift> allShiftsFromLocal = workShiftService.getAll();
        List<BatchShiftFailed> allShiftsFromLocalFailed = workShiftService.getAllFailed();

        List<Integer> allIds = new ArrayList<>(allShiftsFromRestEndpoint.stream().map(ShiftDto::getId).collect(Collectors.toList()));
        List<Integer> processedIds = new ArrayList<>(allShiftsFromLocal.stream().map(Shift::getId).collect(Collectors.toList()));
        List<Integer> failedIds = new ArrayList<>(allShiftsFromLocalFailed.stream().map(BatchShiftFailed::getShiftId).collect(Collectors.toList()));

        allIds.removeAll(processedIds);
        allIds.removeAll(failedIds);

        if (allIds.size() > 0){

            String ids = allIds.stream().map(id -> (id.toString())).collect(Collectors.joining(","));
            List<ShiftDto> dtos = clientService.getSome(ids);
            log.info(String.format("about to create a batch for ids: %s",ids));
            Batch batch = workShiftService.executeBatch(dtos);
            log.info(String.format("batch %s done",batch.getId(),ids));

        }else{
            log.info("nothing to run");
        }

    }
}
