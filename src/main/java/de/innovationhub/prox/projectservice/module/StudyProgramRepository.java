package de.innovationhub.prox.projectservice.module;


import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyProgramRepository
    extends CrudRepository<StudyProgram, UUID>, StudyProgramRepositoryCustom {}
