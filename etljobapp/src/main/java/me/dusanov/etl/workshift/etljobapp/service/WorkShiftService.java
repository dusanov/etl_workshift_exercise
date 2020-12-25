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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    /* Transactions are not tested for Redis repos */
    @Transactional(propagation = Propagation.REQUIRED,
                    rollbackFor = Exception.class)
    public Batch executeBatch(List<ShiftDto> shiftDtoList) {
        Batch batch = batchRepo.save(new Batch());
        shiftDtoList.forEach( dto -> {
            try { saveShift(dto,batch); }
            catch (Exception e){
                log.error("caught error in batch save: " + e.getMessage());
                try {
                    shiftFailedRepo.save(
                        new BatchShiftFailed(dto.getId(),e.getMessage(),mapper.writeValueAsString(dto), batch.getId()));
                  log.info("Saved new BatchShiftFailed");
                } catch (Exception fatal){
                  log.error(String.format("fatal error happened for batchId %s, shiftId %s: %s",batch.getId(),dto.getId(), fatal.getMessage()),fatal);
                }
            }
        });
        return batch;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
                  rollbackFor = Exception.class)
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

    public List<Shift> getAll() {
        return (List<Shift>) shiftRepo.findAll();
    }
    public List<BatchShiftFailed> getAllFailed() {
        return (List<BatchShiftFailed>) shiftFailedRepo.findAll();
    }
}
