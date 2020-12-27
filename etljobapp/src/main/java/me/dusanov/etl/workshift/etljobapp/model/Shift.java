package me.dusanov.etl.workshift.etljobapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.config.EST_TZ_Date;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Date;

@RedisHash("shifts")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Shift extends AEtlModel {

    private final static long serialVersionUID = -4472707390104603353L;

    public Shift(ShiftDto shiftDto, String batchId) {
        this.id = shiftDto.getId();
        this.batchId = batchId;
        this.timesheetId = shiftDto.getTimesheetId();
        this.userId = shiftDto.getUserId();
        this.date = shiftDto.getDate();
        if (null != shiftDto.getStart())
            this.start = new EST_TZ_Date(shiftDto.getStart() * timestampMilliMultiplier);
        if (null != shiftDto.getFinish())
            this.finish = new EST_TZ_Date(shiftDto.getFinish() * timestampMilliMultiplier);
        this.departmentId = shiftDto.getDepartmentId();
        this.subCostCentre = shiftDto.getSubCostCentre();
        this.tag = shiftDto.getTag();
        this.tagId = shiftDto.getTagId();
        this.status = shiftDto.getStatus();
        this.metadata = shiftDto.getMetadata();
        this.leaveRequestId = shiftDto.getLeaveRequestId();
        this.shiftFeedbackId = shiftDto.getShiftFeedbackId();
        this.approvedBy = shiftDto.getApprovedBy();
        if (null != shiftDto.getApprovedAt())
            this.approvedAt = new EST_TZ_Date(Long.parseLong(shiftDto.getApprovedAt()) * timestampMilliMultiplier);
        this.cost = shiftDto.getCost();
        this.awardCost = shiftDto.getCostBreakdown().getAwardCost();
        this.allowanceCost = shiftDto.getCostBreakdown().getAllowanceCost();
        if (null != shiftDto.getUpdatedAt())
            this.updatedAt = new EST_TZ_Date(shiftDto.getUpdatedAt() * timestampMilliMultiplier);
        this.recordId = shiftDto.getRecordId();
        if (null != shiftDto.getLastCostedAt())
            this.lastCostedAt = new EST_TZ_Date(shiftDto.getLastCostedAt() * timestampMilliMultiplier);
    }

    @Id
    private Integer id;
    @Indexed
    private Integer timesheetId;
    private Integer userId;
    private String date;
    private EST_TZ_Date start;
    private EST_TZ_Date finish;
    private Integer departmentId;
    private String subCostCentre;
    private String tag;
    private Integer tagId;
    private String status;
    private String metadata;
    private Integer leaveRequestId;
    private Integer shiftFeedbackId;
    private Integer approvedBy;
    private EST_TZ_Date approvedAt;
    private Double cost;
    private Double awardCost;
    private Double allowanceCost;
    private EST_TZ_Date updatedAt;
    private Integer recordId;
    private EST_TZ_Date lastCostedAt;
    //many to one
    private String batchId;
}