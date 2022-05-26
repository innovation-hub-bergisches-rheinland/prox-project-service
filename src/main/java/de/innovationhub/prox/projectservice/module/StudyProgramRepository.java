package de.innovationhub.prox.projectservice.module;


import de.innovationhub.prox.projectservice.module.projection.StudyProgramWithModulesProjection;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "studyPrograms")
@RepositoryRestResource(excerptProjection = StudyProgramWithModulesProjection.class)
public interface StudyProgramRepository
    extends CrudRepository<StudyProgram, UUID>, StudyProgramRepositoryCustom {}
