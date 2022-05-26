package de.innovationhub.prox.projectservice.module;


import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

public interface StudyProgramRepository
    extends CrudRepository<StudyProgram, UUID>, StudyProgramRepositoryCustom {}
