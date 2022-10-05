package de.innovationhub.prox.projectservice.module;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.DatabaseIntegrationTest;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("testcontainers-postgres")
@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DirtiesContext
class ModuleTypeRepositoryTest extends DatabaseIntegrationTest {

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
