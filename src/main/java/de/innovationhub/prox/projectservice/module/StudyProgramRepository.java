package de.innovationhub.prox.projectservice.module;


import de.innovationhub.prox.projectservice.module.projection.StudyProgramExcerpt;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "studyPrograms")
@RepositoryRestResource(excerptProjection = StudyProgramExcerpt.class)
public interface StudyProgramRepository
    extends CrudRepository<StudyProgram, UUID>, StudyProgramRepositoryCustom {}
