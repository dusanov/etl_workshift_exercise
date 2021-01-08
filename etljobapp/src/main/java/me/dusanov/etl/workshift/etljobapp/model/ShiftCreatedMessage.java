package me.dusanov.etl.workshift.etljobapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
public class ShiftCreatedMessage implements Serializable {

    private Integer id;
}
