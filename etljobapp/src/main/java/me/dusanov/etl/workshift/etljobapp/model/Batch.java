package me.dusanov.etl.workshift.etljobapp.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
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
