package me.dusanov.etl.workshift.etljobapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;
import java.util.UUID;

@RedisHash("batches")
@Data
@EqualsAndHashCode(callSuper = true)
public class Batch extends AEtlModel {

    private final static long serialVersionUID = -4671670169084511353L;

    public Batch(){
        this.id = UUID.randomUUID().toString();
        this.dateCreated = new Date();
    }

    @Id
    private String id;
    private Date dateCreated;
}
