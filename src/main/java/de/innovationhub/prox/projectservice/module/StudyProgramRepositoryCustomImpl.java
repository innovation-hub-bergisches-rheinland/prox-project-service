package de.innovationhub.prox.projectservice.module;


import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class StudyProgramRepositoryCustomImpl implements StudyProgramRepositoryCustom {
  @Autowired @Lazy private StudyProgramRepository studyProgramRepository;

  @Override
  public Iterable<ModuleType> findAllModulesOfStudyPrograms(UUID[] studyProgramIds) {
    return StreamSupport.stream(
            studyProgramRepository.findAllById(Arrays.asList(studyProgramIds)).spliterator(), false)
        .map(StudyProgram::getModules)
        .flatMap(moduleTypes -> Arrays.stream(moduleTypes.toArray(new ModuleType[] {})))
        .collect(Collectors.toSet());
  }
}
