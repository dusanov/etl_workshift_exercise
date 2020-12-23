package me.dusanov.etl.workshift.etljobapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.BreakDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "shift_breaks")
@Data
@NoArgsConstructor
public class Break {
    public Break(BreakDto breakDto, Integer id, String date, Integer timesheetId) {

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
