package me.dusanov.etl.workshift.etljobapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "shifts")
@Data
@NoArgsConstructor
public class Shift implements Serializable {

    private final static long serialVersionUID = -4472707390104603353L;

    public Shift(ShiftDto shiftDto) {
        this.id = shiftDto.getId();
        this.timesheetId = shiftDto.getTimesheetId();
        this.userId = shiftDto.getUserId();
        this.date = shiftDto.getDate();
        this.start = shiftDto.getStart();
        this.finish = shiftDto.getFinish();
        this.departmentId = shiftDto.getDepartmentId();
        this.subCostCentre = shiftDto.getSubCostCentre();
        this.tag = shiftDto.getTag();
        this.tagId = shiftDto.getTagId();
        this.status = shiftDto.getStatus();
        this.metadata = shiftDto.getMetadata();
        this.leaveRequestId = shiftDto.getLeaveRequestId();
        this.shiftFeedbackId = shiftDto.getShiftFeedbackId();
        this.approvedBy = shiftDto.getApprovedBy();
        this.approvedAt = shiftDto.getApprovedAt();
        this.cost = shiftDto.getCost();
        this.awardCost = shiftDto.getCostBreakdown().getAwardCost();
        this.allowanceCost = shiftDto.getCostBreakdown().getAllowanceCost();
        this.updatedAt = shiftDto.getUpdatedAt();
        this.recordId = shiftDto.getRecordId();
        this.lastCostedAt = shiftDto.getLastCostedAt();
    }

    @Id
    private Integer id;
    private Integer timesheetId;
    private Integer userId;
    private String date;
    private Integer start;
    private Integer finish;
    private Integer departmentId;
    private String subCostCentre;
    private String tag;
    private Integer tagId;
    private String status;
    private String metadata;
    private Integer leaveRequestId;
    private Integer shiftFeedbackId;
    private Integer approvedBy;
    //TODO: this needs to be converted but it's a string
    private String approvedAt;
    private Double cost;
    private Double awardCost;
    private Double allowanceCost;
    private Integer updatedAt;
    private Integer recordId;
    private Integer lastCostedAt;
}