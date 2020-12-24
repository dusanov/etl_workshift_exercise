package me.dusanov.etl.workshift.etljobapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.AllowanceDto;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.util.Date;

@RedisHash("shifts_allowances")
@Data
@NoArgsConstructor
public class Allowance {

    public Allowance(AllowanceDto dto, Integer shiftId, String shiftDate, Integer timesheetId) {
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

    @Id
    private Integer id;
    // foreign key to shift.id
    private Integer shiftId;
    //shift_date (corresponds to ‘date’ in shift object)
    private String shiftDate;
    //sheet_id (corresponds to ‘sheet_id’ in shift object);
    private Integer timesheetId;
    private String name;
    private Double value;
    private Date updatedAt;
    private Double cost;
}
