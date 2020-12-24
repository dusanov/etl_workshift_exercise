package me.dusanov.etl.workshift.etljobapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.BreakDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


import java.util.Date;

@RedisHash("shifts_breaks")
@Data
@NoArgsConstructor
public class Break extends AEtlModel {

    private final static long serialVersionUID = -4035167523827984655L;

    public Break(BreakDto breakDto, Integer id, String date, Integer timesheetId) {

        this.id = breakDto.getId();
        this.shiftId = id;
        this.shiftDate = date;
        this.timesheetId = timesheetId;
        if (null != breakDto.getStart())
            this.start = new Date(breakDto.getStart() * multiplier);
        if (null != breakDto.getFinish())
            this.finish = new Date(breakDto.getFinish() * multiplier);
        this.length = breakDto.getLength();
        this.paid = breakDto.getPaid();
        if (null != breakDto.getUpdatedAt())
            this.updatedAt = new Date(breakDto.getUpdatedAt() * multiplier);
    }

    @Id
    private Integer id;
    // foreign key to shift.id
    private Integer shiftId;
    //shift_date (corresponds to ‘date’ in shift object)
    private String shiftDate;
    //sheet_id (corresponds to ‘sheet_id’ in shift object);
    private Integer timesheetId;
    private Date start;
    private Date finish;
    private Integer length;
    private Boolean paid;
    private Date updatedAt;
}
