package me.dusanov.etl.workshift.etljobapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.AllowanceDto;

import javax.persistence.*;
import java.util.Date;
import java.util.TimeZone;

@Entity
@Table(name="shift_allowances")
@Data
@NoArgsConstructor
public class Allowance {

    public Allowance(AllowanceDto dto, Integer shiftId, String shiftDate, Integer timesheetId ) {
        //we want EST date time
        TimeZone.setDefault(TimeZone.getTimeZone("EST"));

        this.id = dto.getId();
        this.shiftId = shiftId;
        this.shiftDate = shiftDate;
        this.timesheetId = timesheetId;
        this.name = dto.getName();
        this.value = dto.getValue();
        if (null != dto.getUpdatedAt())
            this.updatedAt = new Date(dto.getUpdatedAt() * 1000L);
        this.cost = dto.getCost();
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
    private String name;
    private Double value;
    private Date updatedAt;
    private Double cost;
}
