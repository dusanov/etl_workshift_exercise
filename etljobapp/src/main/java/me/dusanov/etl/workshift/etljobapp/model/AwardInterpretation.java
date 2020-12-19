package me.dusanov.etl.workshift.etljobapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.AwardInterpretationDto;

import javax.persistence.*;
import java.util.Date;
import java.util.TimeZone;

@Entity
@Table(name = "shift_award_interpretations")
@Data
@NoArgsConstructor
public class AwardInterpretation {

    public AwardInterpretation(AwardInterpretationDto awardInterpretationDto, Integer id, String date, Integer timesheetId) {
        //we want EST date time
        TimeZone.setDefault(TimeZone.getTimeZone("EST"));

        this.shiftId = id;
        this.shiftDate = date;
        this.timesheetId = timesheetId;
        this.units = awardInterpretationDto.getUnits();
        this.date = awardInterpretationDto.getDate();
        this.exportName = awardInterpretationDto.getExportName();
        this.secondaryExportName = awardInterpretationDto.getSecondaryExportName();
        this.ordinaryHours = awardInterpretationDto.getOrdinaryHours();
        this.cost = awardInterpretationDto.getCost();
        if (null != awardInterpretationDto.getFrom())
            this.from = new Date(awardInterpretationDto.getFrom() * 1000L);
        if (null != awardInterpretationDto.getTo())
            this.to = new Date(awardInterpretationDto.getTo() * 1000L);
    }

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
    private Date from;
    private Date to;
}
