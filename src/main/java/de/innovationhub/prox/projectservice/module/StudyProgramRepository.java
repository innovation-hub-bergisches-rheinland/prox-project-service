package de.innovationhub.prox.projectservice.module;


import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyProgramRepository
    extends CrudRepository<StudyProgram, UUID>, StudyProgramRepositoryCustom {}
