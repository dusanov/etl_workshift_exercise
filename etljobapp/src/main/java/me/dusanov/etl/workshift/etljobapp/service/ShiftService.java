package me.dusanov.etl.workshift.etljobapp.service;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepo shiftRepo;
    private final BreakRepo breakRepo;
    private final AwardInterpretationRepo awardInterpretationRepo;
    private final AllowanceRepo allowanceRepo;

    @Transactional
    public void save(ShiftDto shiftDto){

        Shift shift = new Shift(shiftDto);
        shiftRepo.save(shift);

        for (AllowanceDto allowanceDto : shiftDto.getAllowances()){
            Allowance allowance = new Allowance(allowanceDto,shift.getId(),shift.getDate(),shift.getTimesheetId());
            allowanceRepo.save(allowance);
        }

        for (AwardInterpretationDto awardInterpretationDto : shiftDto.getAwardInterpretation()){
            AwardInterpretation aw = new AwardInterpretation(awardInterpretationDto, shift.getId(),shift.getDate(),shift.getTimesheetId());
            awardInterpretationRepo.save(aw);
        }

        for (BreakDto breakDto : shiftDto.getBreaks()){
            Break brejk = new Break(breakDto, shift.getId(),shift.getDate(),shift.getTimesheetId());
            breakRepo.save(brejk);
        }
    }
}