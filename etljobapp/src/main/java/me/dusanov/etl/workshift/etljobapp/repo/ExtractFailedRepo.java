package me.dusanov.etl.workshift.etljobapp.repo;

import me.dusanov.etl.workshift.etljobapp.model.BatchExtractFailed;
import me.dusanov.etl.workshift.etljobapp.model.BatchShiftFailed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtractFailedRepo extends CrudRepository<BatchExtractFailed,Integer> {}
