package de.innovationhub.prox.projectservice.module;


import java.util.UUID;

public interface StudyProgramRepositoryCustom {
  Iterable<ModuleType> findAllModulesOfStudyPrograms(UUID[] studyProgramIds);
}
