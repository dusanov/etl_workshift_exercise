package me.dusanov.etl.workshift.etljobapp.repo;

import me.dusanov.etl.workshift.etljobapp.model.Allowance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//these are obsolete, using EM instead to avoid select before insert perf concern
@Deprecated
@Repository
public interface AllowanceRepo extends CrudRepository<Allowance,Integer> {}
