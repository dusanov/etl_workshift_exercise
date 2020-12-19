package me.dusanov.etl.workshift.etljobapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.AwardInterpretationDto;

import javax.persistence.*;

@Entity
@Table(name = "award_interpretations")
@Data
@NoArgsConstructor
public class AwardInterpretation {

    public AwardInterpretation(AwardInterpretationDto awardInterpretationDto, Integer id, String date, Integer timesheetId) {
        this.shiftId = id;
        this.shiftDate = date;
        this.timesheetId = timesheetId;
        this.units = awardInterpretationDto.getUnits();
        this.date = awardInterpretationDto.getDate();
        this.exportName = awardInterpretationDto.getExportName();
        this.secondaryExportName = awardInterpretationDto.getSecondaryExportName();
        this.ordinaryHours = awardInterpretationDto.getOrdinaryHours();
        this.cost = awardInterpretationDto.getCost();
        this.from = awardInterpretationDto.getFrom();
        this.to = awardInterpretationDto.getTo();
    }

    @Id @GeneratedValue
    private Integer id;
    // foreign key to shift.id
    private Integer shiftId;
    //shift_date (corresponds to ‘date’ in shift object)
    private String shiftDate;
    //sheet_id (corresponds to ‘sheet_id’ in shift object);
    @Column(name="sheet_id")
    private Integer timesheetId;
    private Double units;
    private String date;
    private String exportName;
    private String secondaryExportName;
    private Boolean ordinaryHours;
    private Double cost;
    @Column(name="\"from\"")
    private Integer from;
    private Integer to;
}
