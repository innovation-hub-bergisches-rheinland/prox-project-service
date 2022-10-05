package de.innovationhub.prox.projectservice.module;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.AbstractDatabaseIT;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ModuleTypeRepositoryIT extends AbstractDatabaseIT {

  @Autowired ModuleTypeRepository moduleTypeRepository;

  @Autowired EntityManager entityManager;

  @Test
  void findAllModuleTypesOfSpecializationId() {
    // Given
    var specialization = new Specialization("TST", "Test Specialization");
    var moduleTypesOfSpecialization =
        Set.of(
            new ModuleType("T1", "Test 1"),
            new ModuleType("T2", "Test 2"),
            new ModuleType("T3", "Test 3"));
    var otherModuleTypes =
        Set.of(
            new ModuleType("T4", "Test 4"),
            new ModuleType("T5", "Test 5"),
            new ModuleType("T6", "Test 6"));
    var studyProgram =
        new StudyProgram("TST", "Test StudyProgram", moduleTypesOfSpecialization, specialization);
    studyProgram.setModules(moduleTypesOfSpecialization);

    entityManager.persist(specialization);
    moduleTypesOfSpecialization.forEach(entityManager::persist);
    otherModuleTypes.forEach(entityManager::persist);
    entityManager.persist(studyProgram);
    entityManager.flush();

    // When
    var result =
        moduleTypeRepository.findAllModuleTypesOfSpecializationId(Set.of(specialization.getKey()));

    // Then
    assertThat(result).containsExactlyInAnyOrderElementsOf(moduleTypesOfSpecialization);
  }

  @Test
  void findSpecializationsOfModules() {
    // Given
    var specialization = new Specialization("TST", "Test Specialization");
    var moduleTypesOfSpecialization =
        Set.of(
            new ModuleType("T1", "Test 1"),
            new ModuleType("T2", "Test 2"),
            new ModuleType("T3", "Test 3"));
    var otherModuleTypes =
        Set.of(
            new ModuleType("T4", "Test 4"),
            new ModuleType("T5", "Test 5"),
            new ModuleType("T6", "Test 6"));
    var studyProgram =
        new StudyProgram("TST", "Test StudyProgram", moduleTypesOfSpecialization, specialization);
    studyProgram.setModules(moduleTypesOfSpecialization);

    entityManager.persist(specialization);
    moduleTypesOfSpecialization.forEach(entityManager::persist);
    otherModuleTypes.forEach(entityManager::persist);
    entityManager.persist(studyProgram);
    entityManager.flush();

    // When
    var result =
        moduleTypeRepository.findSpecializationsOfModules(
            moduleTypesOfSpecialization.stream()
                .map(ModuleType::getKey)
                .collect(Collectors.toSet()));

    // Then
    assertThat(result).hasSize(1).containsExactly(specialization);
  }
}
