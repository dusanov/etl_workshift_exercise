package me.dusanov.etl.workshift.etljobapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.dusanov.etl.workshift.etljobapp.EtljobappApplication;
import me.dusanov.etl.workshift.etljobapp.dto.AllowanceDto;
import me.dusanov.etl.workshift.etljobapp.dto.AwardInterpretationDto;
import me.dusanov.etl.workshift.etljobapp.dto.BreakDto;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.*;
import me.dusanov.etl.workshift.etljobapp.repo.AllowanceRepo;
import me.dusanov.etl.workshift.etljobapp.repo.AwardInterpretationRepo;
import me.dusanov.etl.workshift.etljobapp.repo.BreakRepo;
import me.dusanov.etl.workshift.etljobapp.repo.ShiftRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "workshift.endpoint")
public class ShiftService {

    private static final Logger log = LoggerFactory.getLogger(ShiftService.class);

    /* these are obsolete, using EM instead to avoid select before insert perf concern
    private final ShiftRepo shiftRepo;
    private final BreakRepo breakRepo;
    private final AwardInterpretationRepo awardInterpretationRepo;
    private final AllowanceRepo allowanceRepo;
    */

    @Getter
    @Setter
    private String timezone;
    @Autowired private final EntityManager entitymanager;
    private final ObjectMapper mapper = new ObjectMapper();


    @Transactional
    public void saveBatch(Batch batch, List<ShiftDto> shiftDtoList) throws JsonProcessingException {

        entitymanager.persist(batch);

        for (ShiftDto dto : shiftDtoList){
            try {
                saveShift(dto,batch);
            }catch (Exception e){
                entitymanager.persist(
                        new BatchShiftFailed(dto.getId(),e.getMessage(),mapper.writeValueAsString(dto), batch.getId()));
            }

        }
    }

    @Transactional
    public Shift saveShift(ShiftDto shiftDto, Batch batch) throws Exception {

        try {
            Shift shift = new Shift(shiftDto,batch.getId(),this.timezone);
            entitymanager.persist(shift);
            for (AllowanceDto allowanceDto : shiftDto.getAllowances()) {
                Allowance allowance = new Allowance(allowanceDto, shift.getId(), shift.getDate(), shift.getTimesheetId(),this.timezone);
                entitymanager.persist(allowance);
            }
            for (AwardInterpretationDto awardInterpretationDto : shiftDto.getAwardInterpretation()) {
                AwardInterpretation aw = new AwardInterpretation(awardInterpretationDto, shift.getId(), shift.getDate(), shift.getTimesheetId(),this.timezone);
                entitymanager.persist(aw);
            }
            for (BreakDto breakDto : shiftDto.getBreaks()) {
                Break brejk = new Break(breakDto, shift.getId(), shift.getDate(), shift.getTimesheetId(),this.timezone);
                entitymanager.persist(brejk);
            }
            return shift;
        } catch (Exception e)
        {
            log.error("something very bad happened: " + e.getMessage());
            throw new Exception(e.getMessage(),e);
        }
    }
}