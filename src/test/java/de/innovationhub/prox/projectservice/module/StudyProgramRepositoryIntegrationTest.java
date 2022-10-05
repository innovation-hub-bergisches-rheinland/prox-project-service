package de.innovationhub.prox.projectservice.module;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.AbstractDatabaseIntegrationTest;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StudyProgramRepositoryIntegrationTest extends AbstractDatabaseIntegrationTest {

  @Autowired
  StudyProgramRepository studyProgramRepository;

  @Autowired
  EntityManager em;

  @Test
  void shouldReturnModule_findAllModulesOfStudyPrograms() {
    var moduleType = new ModuleType("TST", "Test");
    em.persist(moduleType);
    var specialization = new Specialization("TST", "Test");
    em.persist(specialization);
    var studyProgram1 = new StudyProgram("TST", "Test", Set.of(moduleType), specialization);
    em.persist(studyProgram1);

    var result = studyProgramRepository.findAllModulesOfStudyPrograms(List.of(studyProgram1.getId()));

    assertThat(result)
      .hasSize(1)
      .first()
      .isEqualTo(moduleType);
  }

  @Test
  void shouldRemoveDuplicatesOn_findAllModulesOfStudyPrograms() {
    var moduleType = new ModuleType("TST", "Test");
    em.persist(moduleType);
    var specialization = new Specialization("TST", "Test");
    em.persist(specialization);
    var studyProgram1 = new StudyProgram("TST", "Test", Set.of(moduleType), specialization);
    var studyProgram2 = new StudyProgram("TST2", "Test 2", Set.of(moduleType), specialization);
    em.persist(studyProgram1);
    em.persist(studyProgram2);

    var result = studyProgramRepository.findAllModulesOfStudyPrograms(List.of(studyProgram1.getId(), studyProgram2.getId()));

    assertThat(result)
      .hasSize(1);
  }
}
