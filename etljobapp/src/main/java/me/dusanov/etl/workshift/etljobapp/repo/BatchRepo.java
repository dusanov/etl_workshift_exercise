package me.dusanov.etl.workshift.etljobapp.repo;

import me.dusanov.etl.workshift.etljobapp.model.Batch;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Deprecated
@Repository
public interface BatchRepo extends CrudRepository<Batch,String> {}
