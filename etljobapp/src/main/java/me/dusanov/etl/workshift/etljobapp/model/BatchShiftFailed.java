package me.dusanov.etl.workshift.etljobapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "batches_shifts_failed")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchShiftFailed {
    private final static long serialVersionUID = -4671670169084511353L;

    @Id
    private Integer shiftId;
    private String errorMessage;
    @Lob
    private String dto;
    private String batchId;
}
