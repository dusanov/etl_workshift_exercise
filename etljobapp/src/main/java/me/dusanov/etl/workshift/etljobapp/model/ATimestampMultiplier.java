package me.dusanov.etl.workshift.etljobapp.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

public abstract class ATimestampMultiplier {
    long multiplier = 1000L;
}
