package me.dusanov.etl.workshift.etljobapp.repo;

import me.dusanov.etl.workshift.etljobapp.model.Shift;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRepo extends CrudRepository<Shift,Integer> {}