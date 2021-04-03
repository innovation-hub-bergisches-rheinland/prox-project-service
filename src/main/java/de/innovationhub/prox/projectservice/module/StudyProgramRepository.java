package de.innovationhub.prox.projectservice.module;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface StudyProgramRepository extends CrudRepository<StudyProgram, UUID>, StudyProgramRepositoryCustom {
}
