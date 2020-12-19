package me.dusanov.etl.workshift.etljobapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<BreakDto> breaks;
    private Integer finish;
    private Integer departmentId;
    private String subCostCentre;
    private String tag;
    private Integer tagId;
    private String status;
    private String metadata;
    private Integer leaveRequestId;
    private List<AllowanceDto> allowances;
    private Integer shiftFeedbackId;
    private Integer approvedBy;
    private String approvedAt;
    private List<AwardInterpretationDto> awardInterpretation;
    private Double cost;
    private CostBreakdownDto costBreakdown;
    private Integer updatedAt;
    private Integer recordId;
    private Integer lastCostedAt;
}