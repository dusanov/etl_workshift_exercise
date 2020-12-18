package me.dusanov.etl.workshift.etljobapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftDto implements Serializable {

    private final static long serialVersionUID = 3586204739591817858L;

    private Integer id;
    private Integer timesheetId;
    private Integer userId;
    private String date;
    private Integer start;
    private List<Break> breaks;
    private Integer finish;
    private Integer departmentId;
    private Object subCostCentre;
    private Object tag;
    private Object tagId;
    private String status;
    private Object metadata;
    private Object leaveRequestId;
    private List<Allowance> allowances;
    private Object shiftFeedbackId;
    private Object approvedBy;
    private Object approvedAt;
    private List<AwardInterpretation> awardInterpretation;
    private Double cost;
    private CostBreakdown costBreakdown;
    private Integer updatedAt;
    private Integer recordId;
    private Integer lastCostedAt;
}