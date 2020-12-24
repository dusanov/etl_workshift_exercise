package me.dusanov.etl.workshift.etljobapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.AllowanceDto;
import me.dusanov.etl.workshift.etljobapp.dto.AwardInterpretationDto;
import me.dusanov.etl.workshift.etljobapp.dto.BreakDto;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.*;
import me.dusanov.etl.workshift.etljobapp.repo.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        Batch batch = new Batch();
        batchRepo.save(batch);
        for (ShiftDto dto : shiftDtoList){
            try {
                saveShift(dto,batch);
            }catch (Exception e){
                log.error("caught error in batch save: " + e.getMessage());
                try {
                    shiftFailedRepo.save(
                        new BatchShiftFailed(dto.getId(),e.getMessage(),mapper.writeValueAsString(dto), batch.getId()));
                  log.info("Saved new BatchShiftFailed");
                } catch (Exception fatal){
                  log.error("fatal error happened: "+ fatal.getMessage(),fatal);
                }
            }
        }
        return batch;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
                  rollbackFor = Exception.class)
    public Shift saveShift(ShiftDto shiftDto, @NotNull Batch batch) {
        Shift shift = new Shift(shiftDto,batch.getId());
        try {
            shiftRepo.save(shift);

            List<Allowance> allowances = new ArrayList<>();
            List<AwardInterpretation> awardInterpretations = new ArrayList<>();
            List<Break> breaks = new ArrayList<>();

            for (AllowanceDto allowanceDto : shiftDto.getAllowances()) {
                Allowance allowance = new Allowance(allowanceDto, shift.getId(), shift.getDate(), shift.getTimesheetId());
                allowances.add(allowance);
            }
            allowanceRepo.saveAll(allowances);

            for (AwardInterpretationDto awardInterpretationDto : shiftDto.getAwardInterpretation()) {
                AwardInterpretation aw = new AwardInterpretation(awardInterpretationDto, shift.getId(), shift.getDate(), shift.getTimesheetId());
                awardInterpretations.add(aw);
            }
            awardInterpretationRepo.saveAll(awardInterpretations);

            for (BreakDto breakDto : shiftDto.getBreaks()) {
                Break brejk = new Break(breakDto, shift.getId(), shift.getDate(), shift.getTimesheetId());
                breaks.add(brejk);
            }
            breakRepo.saveAll(breaks);
        } catch (Exception e)
        {
            log.error("something bad happened: " + e.getMessage());
            throw e;
        }
        return shift;
    }

    public List<Shift> getAll() {
        return (List<Shift>) shiftRepo.findAll();
    }

    public List<BatchShiftFailed> getAllFailed() {
        return (List<BatchShiftFailed>) shiftFailedRepo.findAll();
    }
}
