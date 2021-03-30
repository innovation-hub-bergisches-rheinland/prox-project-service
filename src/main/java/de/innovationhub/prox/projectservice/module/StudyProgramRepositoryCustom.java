package de.innovationhub.prox.projectservice.module;

import java.util.UUID;
import org.springframework.stereotype.Repository;

public interface StudyProgramRepositoryCustom {
  Iterable<ModuleType> findAllModulesOfStudyPrograms(UUID[] studyProgramIds);
}
