package me.dusanov.etl.workshift.etljobapp.repo;

import me.dusanov.etl.workshift.etljobapp.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//these are obsolete, using EM instead to avoid select before insert perf concern
@Deprecated
@Repository
public interface ShiftRepo extends CrudRepository<Shift,Integer> {}
//public interface ShiftRepo extends JpaRepository<Shift,Integer> {}
