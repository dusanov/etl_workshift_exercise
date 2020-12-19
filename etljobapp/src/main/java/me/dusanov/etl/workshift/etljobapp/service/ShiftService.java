package me.dusanov.etl.workshift.etljobapp.service;

import lombok.RequiredArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.EtljobappApplication;
import me.dusanov.etl.workshift.etljobapp.dto.AllowanceDto;
import me.dusanov.etl.workshift.etljobapp.dto.AwardInterpretationDto;
import me.dusanov.etl.workshift.etljobapp.dto.BreakDto;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.Allowance;
import me.dusanov.etl.workshift.etljobapp.model.AwardInterpretation;
import me.dusanov.etl.workshift.etljobapp.model.Break;
import me.dusanov.etl.workshift.etljobapp.model.Shift;
import me.dusanov.etl.workshift.etljobapp.repo.AllowanceRepo;
import me.dusanov.etl.workshift.etljobapp.repo.AwardInterpretationRepo;
import me.dusanov.etl.workshift.etljobapp.repo.BreakRepo;
import me.dusanov.etl.workshift.etljobapp.repo.ShiftRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private static final Logger log = LoggerFactory.getLogger(ShiftService.class);

    /* these are obsolete, using EM instead to avoid select before insert perf concern
    private final ShiftRepo shiftRepo;
    private final BreakRepo breakRepo;
    private final AwardInterpretationRepo awardInterpretationRepo;
    private final AllowanceRepo allowanceRepo;
    */

    @Autowired private final EntityManager entitymanager;

    @Transactional
    public void save(ShiftDto shiftDto) {

        try {
            log.info(" === before save new shift");
            Shift shift = new Shift(shiftDto);
            //shiftRepo.save(shift);
            entitymanager.persist(shift);

            log.info(" --- after save new shift");
            log.info(" === before save new allowances");
            for (AllowanceDto allowanceDto : shiftDto.getAllowances()) {
                Allowance allowance = new Allowance(allowanceDto, shift.getId(), shift.getDate(), shift.getTimesheetId());
                //allowanceRepo.save(allowance);
                entitymanager.persist(allowance);
            }
            log.info(" --- after save new allowances");

            for (AwardInterpretationDto awardInterpretationDto : shiftDto.getAwardInterpretation()) {
                AwardInterpretation aw = new AwardInterpretation(awardInterpretationDto, shift.getId(), shift.getDate(), shift.getTimesheetId());
                //awardInterpretationRepo.save(aw);
                entitymanager.persist(aw);
            }

            for (BreakDto breakDto : shiftDto.getBreaks()) {
                Break brejk = new Break(breakDto, shift.getId(), shift.getDate(), shift.getTimesheetId());
                //breakRepo.save(brejk);
                entitymanager.persist(brejk);
            }
        } catch (Exception e)
        {
            log.error(e.toString());
            throw e;
        }
    }
}