package me.dusanov.etl.workshift.etljobapp.model;

import java.io.Serializable;

public abstract class AEtlModel implements Serializable {
    long timestampMultiplier = 1000L;
}
