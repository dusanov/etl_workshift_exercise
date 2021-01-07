package me.dusanov.etl.workshift.etljobapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("shifts_failed")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class BatchExtractFailed extends AEtlModel {

    private final static long serialVersionUID = -4671670169084511353L;

    @Id
    private Integer id;
    private String errorMessage;
    private String extractBody;
    private String batchId;
}
