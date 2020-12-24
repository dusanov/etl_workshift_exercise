package me.dusanov.etl.workshift.etljobapp.repo;

import me.dusanov.etl.workshift.etljobapp.model.BatchShiftFailed;
import me.dusanov.etl.workshift.etljobapp.model.Break;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftFailedRepo extends CrudRepository<BatchShiftFailed,Integer> {}
