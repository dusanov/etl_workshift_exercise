package me.dusanov.etl.workshift.etljobapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.AllowanceDto;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("shifts_allowances")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Allowance extends AEtlModel {

    private final static long serialVersionUID = -6607699404886896923L;

    public Allowance(AllowanceDto dto, Integer shiftId, String shiftDate, Integer timesheetId) {
        this.id = dto.getId();
        this.shiftId = shiftId;
        this.shiftDate = shiftDate;
        this.timesheetId = timesheetId;
        this.name = dto.getName();
        this.value = dto.getValue();
        if (null != dto.getUpdatedAt())
            this.updatedAt = new EST_TZ_Date(dto.getUpdatedAt() * timestampMilliMultiplier);
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
    private EST_TZ_Date updatedAt;
    private Double cost;
}
