package me.dusanov.etl.workshift.etljobapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.*;
import me.dusanov.etl.workshift.etljobapp.repo.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkShiftService {

    private static final Logger log = LoggerFactory.getLogger(WorkShiftService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    private final ShiftRepo shiftRepo;
    private final ShiftFailedRepo shiftFailedRepo;
    private final BatchRepo batchRepo;
    private final BreakRepo breakRepo;
    private final AwardInterpretationRepo awardInterpretationRepo;
    private final AllowanceRepo allowanceRepo;

    public void executeBatch(@NotNull Batch batch, @NotNull List<ShiftDto> shiftDtoList) {
        shiftDtoList.forEach( dto -> {
            try { saveShift(dto,batch); }
            catch (RuntimeException e){
                log.error("caught error in saveShift: " + e.getMessage());
                try {
                    shiftFailedRepo.save(
                        new BatchShiftFailed(dto.getId(),e.getMessage(),mapper.writeValueAsString(dto), batch.getId()));
                  log.error("BatchShiftFailed has been saved");
                } catch (Exception fatal){
                  log.error(String.format("fatal error happened for batchId %s, shiftId %s: %s\nroot cause: %s\ndto: %s",
                                            batch.getId(), dto.getId(), fatal.getMessage(), e.getMessage(), dto), e);
                }
            }
        });
    }

    public Shift saveShift(@NotNull ShiftDto shiftDto, @NotNull Batch batch) {

        Shift shift = shiftRepo.save(new Shift(shiftDto,batch.getId()));
        allowanceRepo.saveAll((shiftDto.getAllowances().stream()
                        .map(dto -> new Allowance(
                                dto,
                                shift.getId(),
                                shift.getDate(),
                                shift.getTimesheetId()))
                        .collect(Collectors.toList())));

        awardInterpretationRepo.saveAll((shiftDto.getAwardInterpretation().stream()
                        .map(dto -> new AwardInterpretation(
                                dto,
                                shift.getId(),
                                shift.getDate(),
                                shift.getTimesheetId()))
                        .collect(Collectors.toList())));

        breakRepo.saveAll(shiftDto.getBreaks().stream()
                .map(dto -> new Break(
                        dto,
                        shift.getId(),
                        shift.getDate(),
                        shift.getTimesheetId()))
                .collect(Collectors.toList()));

        return shift;
    }

    public Batch createNewBatch(Batch batch) { return batchRepo.save(batch); }
    public List<Shift> getAll() {
        return (List<Shift>) shiftRepo.findAll();
    }
    public List<BatchShiftFailed> getAllFailed() {
        return (List<BatchShiftFailed>) shiftFailedRepo.findAll();
    }
}