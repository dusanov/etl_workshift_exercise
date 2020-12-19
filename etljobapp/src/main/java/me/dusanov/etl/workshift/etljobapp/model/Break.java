package me.dusanov.etl.workshift.etljobapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.BreakDto;

import javax.persistence.*;

@Entity
@Table(name = "breaks")
@Data
@NoArgsConstructor
public class Break {
    public Break(BreakDto breakDto, Integer id, String date, Integer timesheetId) {
        this.id = breakDto.getId();
        this.shiftId = id;
        this.shiftDate = date;
        this.timesheetId = timesheetId;
        this.start = breakDto.getStart();
        this.finish = breakDto.getFinish();
        this.length = breakDto.getLength();
        this.paid = breakDto.getPaid();
        this.updatedAt = breakDto.getUpdatedAt();
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
    private Integer start;
    private Integer finish;
    private Integer length;
    private Boolean paid;
    private Integer updatedAt;
}
