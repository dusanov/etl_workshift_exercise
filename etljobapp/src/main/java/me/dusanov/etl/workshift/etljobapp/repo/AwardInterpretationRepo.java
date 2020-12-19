package me.dusanov.etl.workshift.etljobapp.repo;

import me.dusanov.etl.workshift.etljobapp.model.AwardInterpretation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Deprecated
@Repository
public interface AwardInterpretationRepo extends CrudRepository<AwardInterpretation,Integer> {}