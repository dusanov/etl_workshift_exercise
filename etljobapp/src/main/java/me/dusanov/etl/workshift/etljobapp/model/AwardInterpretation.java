package me.dusanov.etl.workshift.etljobapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.config.EST_TZ_Date;
import me.dusanov.etl.workshift.etljobapp.dto.AwardInterpretationDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("shifts_award_interpretations")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AwardInterpretation extends AEtlModel {

    private final static long serialVersionUID = -133437237819658273L;

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
        if (null != awardInterpretationDto.getFrom())
            this.from = new EST_TZ_Date(awardInterpretationDto.getFrom() * timestampMilliMultiplier);
        if (null != awardInterpretationDto.getTo())
            this.to = new EST_TZ_Date(awardInterpretationDto.getTo() * timestampMilliMultiplier);
        //do this last lol
        this.id = this.hashCode();
    }

    @Id
    private Integer id;
    private Integer shiftId;
    private String shiftDate;
    private Integer timesheetId;
    private Double units;
    private String date;
    private String exportName;
    private String secondaryExportName;
    private Boolean ordinaryHours;
    private Double cost;
    private EST_TZ_Date from;
    private EST_TZ_Date to;
}
