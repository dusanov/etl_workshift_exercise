package me.dusanov.etl.workshift.etljobapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.dusanov.etl.workshift.etljobapp.dto.AllowanceDto;
import me.dusanov.etl.workshift.etljobapp.dto.AwardInterpretationDto;
import me.dusanov.etl.workshift.etljobapp.dto.BreakDto;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.*;
import me.dusanov.etl.workshift.etljobapp.repo.ShiftFailedRepo;
import me.dusanov.etl.workshift.etljobapp.repo.ShiftRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
//import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "workshift.endpoint")
public class ShiftService {

    private static final Logger log = LoggerFactory.getLogger(ShiftService.class);

    //these are obsolete, using EM instead to avoid select before insert perf concern
    private final ShiftRepo shiftRepo;
    private final ShiftFailedRepo failedShiftRepo;
    /*
    private final BreakRepo breakRepo;
    private final AwardInterpretationRepo awardInterpretationRepo;
    private final AllowanceRepo allowanceRepo;
    */
    @PersistenceContext
    private final EntityManager entitymanager;
    private final ObjectMapper mapper = new ObjectMapper();


    @Transactional
    public void saveBatch(Batch batch, List<ShiftDto> shiftDtoList) throws JsonProcessingException {

        entitymanager.persist(batch);

        for (ShiftDto dto : shiftDtoList){
            try {
                saveShift(dto,batch);
            }catch (Exception e){
                log.error("caught error in batch save: " + e.getMessage());
                entitymanager.persist(
                        new BatchShiftFailed(dto.getId(),e.getMessage(),mapper.writeValueAsString(dto), batch.getId()));
            }

        }
    }

    @Transactional /*(rollbackFor = Exception.class,
                    isolation = Isolation.DEFAULT,
                    propagation = Propagation.REQUIRES_NEW) */
    public Shift saveShift(ShiftDto shiftDto, Batch batch) throws Exception {
        log.debug(" in save shift ");
        try {
            Shift shift = new Shift(shiftDto,batch.getId());
            entitymanager.persist(shift);
            for (AllowanceDto allowanceDto : shiftDto.getAllowances()) {
                Allowance allowance = new Allowance(allowanceDto, shift.getId(), shift.getDate(), shift.getTimesheetId());
                entitymanager.persist(allowance);
            }
            for (AwardInterpretationDto awardInterpretationDto : shiftDto.getAwardInterpretation()) {
                AwardInterpretation aw = new AwardInterpretation(awardInterpretationDto, shift.getId(), shift.getDate(), shift.getTimesheetId());
                entitymanager.persist(aw);
            }
            for (BreakDto breakDto : shiftDto.getBreaks()) {
                Break brejk = new Break(breakDto, shift.getId(), shift.getDate(), shift.getTimesheetId());
                entitymanager.persist(brejk);
            }

            return shift;
        } catch (Exception e)
        {
            log.error("something very bad happened: " + e.getMessage());
            throw new Exception(e.getMessage(),e);
        }
    }

    public List<Shift> getAll() {
        return (List<Shift>) shiftRepo.findAll();
    }

    public List<BatchShiftFailed> getAllFailed() {
        return (List<BatchShiftFailed>) failedShiftRepo.findAll();
    }
}