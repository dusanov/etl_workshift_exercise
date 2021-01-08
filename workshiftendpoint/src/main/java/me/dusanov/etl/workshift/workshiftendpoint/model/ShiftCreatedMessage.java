package me.dusanov.etl.workshift.workshiftendpoint.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ShiftCreatedMessage implements Serializable {

    private Integer id;
}
