package me.dusanov.etl.workshift.etljobapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.BreakDto;

import javax.persistence.*;
import java.util.Date;
import java.util.TimeZone;

@Entity
@Table(name = "shift_breaks")
@Data
@NoArgsConstructor
public class Break {
    public Break(BreakDto breakDto, Integer id, String date, Integer timesheetId, String timezone) {
        //we want EST date time
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));

        this.id = breakDto.getId();
        this.shiftId = id;
        this.shiftDate = date;
        this.timesheetId = timesheetId;
        if (null != breakDto.getStart())
            this.start = new Date(breakDto.getStart() * 1000L);
        if (null != breakDto.getFinish())
            this.finish = new Date(breakDto.getFinish() * 1000L);
        this.length = breakDto.getLength();
        this.paid = breakDto.getPaid();
        if (null != breakDto.getUpdatedAt())
            this.updatedAt = new Date(breakDto.getUpdatedAt() * 1000L);
    }
    @Id //@GeneratedValue
    private Integer id;
    // foreign key to shift.id
    private Integer shiftId;
    //shift_date (corresponds to ‘date’ in shift object)
    private String shiftDate;
    //sheet_id (corresponds to ‘sheet_id’ in shift object);
    @Column(name="sheet_id")
    private Integer timesheetId;
    private Date start;
    private Date finish;
    private Integer length;
    private Boolean paid;
    private Date updatedAt;
}
