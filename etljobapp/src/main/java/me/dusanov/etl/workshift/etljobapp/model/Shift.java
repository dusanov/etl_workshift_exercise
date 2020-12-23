package me.dusanov.etl.workshift.etljobapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "shifts")
@Data
@NoArgsConstructor
public class Shift implements Serializable {

    private final static long serialVersionUID = -4472707390104603353L;

    public Shift(ShiftDto shiftDto, String batchId) {

        this.batchId = batchId;
        this.id = shiftDto.getId();
        this.timesheetId = shiftDto.getTimesheetId();
        this.userId = shiftDto.getUserId();
        this.date = shiftDto.getDate();
        if (null != shiftDto.getStart())
            this.start = new Date(shiftDto.getStart() * 1000L);
        if (null != shiftDto.getFinish())
            this.finish = new Date(shiftDto.getFinish() * 1000L);
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
            this.approvedAt = new Date(Long.valueOf(shiftDto.getApprovedAt()) * 1000L);
        this.cost = shiftDto.getCost();
        this.awardCost = shiftDto.getCostBreakdown().getAwardCost();
        this.allowanceCost = shiftDto.getCostBreakdown().getAllowanceCost();
        this.updatedAt = new Date(shiftDto.getUpdatedAt() * 1000L);
        this.recordId = shiftDto.getRecordId();
        this.lastCostedAt = new Date(shiftDto.getLastCostedAt() * 1000L);
    }

    @Id
    private Integer id;
    private Integer timesheetId;
    private Integer userId;
    private String date;
    private Date start;
    private Date finish;
    private Integer departmentId;
    private String subCostCentre;
    private String tag;
    private Integer tagId;
    private String status;
    private String metadata;
    private Integer leaveRequestId;
    private Integer shiftFeedbackId;
    private Integer approvedBy;
    private Date approvedAt;
    private Double cost;
    private Double awardCost;
    private Double allowanceCost;
    private Date updatedAt;
    private Integer recordId;
    private Date lastCostedAt;
    //many to one
    private String batchId;
}