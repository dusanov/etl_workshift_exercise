package me.dusanov.etl.workshift.etljobapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@Entity
@Table(name = "batches")
@Data
public class Batch implements Serializable {

    private final static long serialVersionUID = -4671670169084511353L;

    public Batch(){
        this.id = UUID.randomUUID().toString();
        this.dateCreated = new Date();
    }

    @Id
    private String id;
    private Date dateCreated;
}
