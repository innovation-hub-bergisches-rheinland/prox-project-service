package de.innovationhub.prox.projectservice.module;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.AbstractDatabaseIT;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ModuleTypeRepositoryIT extends AbstractDatabaseIT {

  @Autowired ModuleTypeRepository moduleTypeRepository;

  @Autowired EntityManager entityManager;

  @Test
  void shouldFindModuleTypesOfSpecializationId() {
    var specialization = createAndPersistSpecialization();
    var moduleTypes = Set.of(
      createAndPersistModuleType("TST1", "Test Module Type 1")
    );
    createAndPersistStudyProgram(moduleTypes, specialization);

    var result = moduleTypeRepository.findAllModuleTypesOfSpecializationId(Set.of("TST"));

    assertThat(result)
      .containsExactlyInAnyOrderElementsOf(moduleTypes);
  }

  @Test
  void shouldFindSpecializationsOfModule() {
    var specialization = createAndPersistSpecialization();
    var moduleTypes =
        Set.of(
            createAndPersistModuleType("T1", "Test 1")
        );

    createAndPersistStudyProgram(moduleTypes, specialization);

    var result =
        moduleTypeRepository.findSpecializationsOfModules(Set.of("T1"));

    assertThat(result)
      .containsExactly(specialization);
  }

  private Specialization createAndPersistSpecialization() {
    var specialization = new Specialization("TST", "Test Specialization");
    entityManager.persist(specialization);
    return specialization;
  }

  private ModuleType createAndPersistModuleType(String key, String name) {
    var moduleType = new ModuleType(key, name);
    entityManager.persist(moduleType);
    return moduleType;
  }

  private StudyProgram createAndPersistStudyProgram(
      Set<ModuleType> moduleTypes, Specialization specialization) {
    var studyProgram = new StudyProgram("TST", "Test StudyProgram", moduleTypes, specialization);
    entityManager.persist(studyProgram);
    return studyProgram;
  }
}
