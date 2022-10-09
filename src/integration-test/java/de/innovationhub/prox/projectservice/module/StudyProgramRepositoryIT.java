package de.innovationhub.prox.projectservice.module;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.AbstractDatabaseIT;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StudyProgramRepositoryIT extends AbstractDatabaseIT {

  @Autowired
  StudyProgramRepository studyProgramRepository;

  @Autowired
  EntityManager em;

  @Test
  void shouldReturnModule_findAllModulesOfStudyPrograms() {
    var moduleType = createAndPersistModuleType();
    var specialization = createAndPersistSpecialization();
    var studyProgram = createAndPersistStudyProgram("TST", Set.of(moduleType), specialization);

    var result = studyProgramRepository.findAllModulesOfStudyPrograms(List.of(studyProgram.getId()));

    assertThat(result)
      .hasSize(1)
      .first()
      .isEqualTo(moduleType);
  }

  @Test
  void shouldRemoveDuplicatesOn_findAllModulesOfStudyPrograms() {
    var moduleType = createAndPersistModuleType();
    var specialization = createAndPersistSpecialization();
    var studyProgram1 = createAndPersistStudyProgram("TST", Set.of(moduleType), specialization);
    var studyProgram2 = createAndPersistStudyProgram("TST2", Set.of(moduleType), specialization);

    var result = studyProgramRepository
      .findAllModulesOfStudyPrograms(List.of(studyProgram1.getId(), studyProgram2.getId()));

    assertThat(result)
      .hasSize(1);
  }

  private Specialization createAndPersistSpecialization() {
    var specialization = new Specialization("TST", "Test Specialization");
    em.persist(specialization);
    return specialization;
  }

  private ModuleType createAndPersistModuleType() {
    var moduleType = new ModuleType("TST", "Test");
    em.persist(moduleType);
    return moduleType;
  }

  private StudyProgram createAndPersistStudyProgram(String key,
    Set<ModuleType> moduleTypes, Specialization specialization) {
    var studyProgram = new StudyProgram(key, "Test StudyProgram", moduleTypes, specialization);
    em.persist(studyProgram);
    return studyProgram;
  }
}
