package de.innovationhub.prox.projectservice.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.innovationhub.prox.projectservice.module.Module;
import de.innovationhub.prox.projectservice.module.ModuleName;
import de.innovationhub.prox.projectservice.module.ModuleRepository;
import de.innovationhub.prox.projectservice.module.ProjectType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProjectRepositoryTest {
  @Autowired ProjectRepository projectRepository;

  @Autowired ModuleRepository moduleRepository;

  static List<Module> sampleModules = new ArrayList<>();
  static Project sampleProject;
  static List<Project> sampleProjects = new ArrayList<>();

  /** SampleData should be instantiated before all tests. */
  @BeforeAll
  static void createSampleData() {

    // Create new sample Modules and add them to list
    Module module = new Module(new ModuleName("Module 1"), ProjectType.UNDEFINED);
    sampleModules.add(module);

    // Create new sample Project
    sampleProject =
        new Project(
            new ProjectName("Testprojekt"),
            new ProjectShortDescription("Best Proj."),
            new ProjectDescription("Bestes Projekt"),
            ProjectStatus.LAUFEND,
            new ProjectRequirement("PhD"),
            new CreatorID(UUID.randomUUID()),
            new CreatorName("Karl Peter"),
            new SupervisorName("Karl Peter"),
            sampleModules);

    sampleProjects.add(sampleProject);
    sampleProjects.add(
        new Project(
            new ProjectName("Testprojekt 2"),
            new ProjectShortDescription("Best Proj. 2"),
            new ProjectDescription("Bestes Projekt 2"),
            ProjectStatus.LAUFEND,
            new ProjectRequirement("PhD"),
            new CreatorID(UUID.randomUUID()),
            new CreatorName("Karl Peter"),
            new SupervisorName("Karl Peter"),
            sampleModules));
  }

  /**
   * As the test class is a DataJpaTest, after test execution the data is rolled back. However some
   * data need to be persisted in each test.
   */
  @BeforeEach
  void save_nested_entities() {
    // Save SampleModules and check if save was successful
    moduleRepository.saveAll(sampleModules);
    assertEquals(sampleModules.size(), moduleRepository.count());
  }

  /** Saves the sample project and verifies it is saved / found by its id */
  void saveSampleProject() {
    projectRepository.save(sampleProject);

    Optional<Project> optionalProjectFound = projectRepository.findById(sampleProject.getId());
    assertTrue(optionalProjectFound.isPresent());
    Project foundProject = optionalProjectFound.get();
    assertEquals(sampleProject, foundProject);
  }

  @Test
  void when_project_saved_then_found_and_equal() {
    saveSampleProject();
  }

  @Test
  void when_project_updated_then_found_and_equal() {
    saveSampleProject();

    // Copy project to maintain integrity of sampleProject
    Project copiedProject = sampleProject;

    copiedProject.setName(new ProjectName("ChangedName"));
    copiedProject.setCreatorID(new CreatorID(UUID.randomUUID()));

    projectRepository.save(copiedProject);
    Optional<Project> optionalProjectFound = projectRepository.findById(copiedProject.getId());
    assertTrue(optionalProjectFound.isPresent());
    Project foundProject = optionalProjectFound.get();
    assertEquals(copiedProject, foundProject);
  }

  @Test
  void when_project_deleted_then_not_found() {
    saveSampleProject();

    projectRepository.delete(sampleProject);

    Optional<Project> optionalProject = projectRepository.findById(sampleProject.getId());
    assertFalse(optionalProject.isPresent());
  }

  @Test
  void when_project_find_by_ids_is_valid_id_then_is_found() {
    saveSampleProject();
    List<Project> projects = projectRepository.findAllByIds(Arrays.array(sampleProject.getId()));

    assertEquals(1, projects.size());
    assertEquals(sampleProject, projects.get(0));
  }

  @Test
  void when_project_find_by_ids_is_invalid_id_then_is_found() {
    saveSampleProject();
    List<Project> projects = projectRepository.findAllByIds(Arrays.array(UUID.randomUUID()));

    assertEquals(0, projects.size());
  }

  @Test
  void when_project_find_by_modifier_date_yesterday_then_is_found() {
    saveSampleProject();

    Set<Project> foundProjects =
        projectRepository.findAllByModifiedAfter(
            Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
    assertEquals(1, foundProjects.size());
  }

  @Test
  void when_project_find_by_modifier_date_tomorrow_then_is_not_found() {
    saveSampleProject();

    Set<Project> foundProjects =
        projectRepository.findAllByModifiedAfter(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    assertEquals(0, foundProjects.size());
  }

  @Test
  void when_project_find_by_status_is_sampleproject_status_then_found() {
    saveSampleProject();

    Set<Project> projects = projectRepository.findByStatus(sampleProject.getStatus());
    assertEquals(1, projects.size());
  }

  @Test
  void when_project_find_by_status_is_not_sampleproject_status_then_not_found() {
    saveSampleProject();

    // Find existing ProjectStatus from enum which is unequal to the projectStatus of sampleProject
    ProjectStatus projectStatus = null;
    for (ProjectStatus status : ProjectStatus.values()) {
      if (status != sampleProject.getStatus()) {
        projectStatus = status;
        break;
      }
    }
    assertNotEquals(sampleProject.getStatus(), projectStatus); // verify its unequal just for safety

    Set<Project> projects = projectRepository.findByStatus(projectStatus);
    assertEquals(0, projects.size());
  }

  @Test
  void when_project_find_by_supervisor_name_containing_full_supervisor_name_then_found() {
    saveSampleProject();

    Set<Project> projects =
        projectRepository.findBySupervisorName_SupervisorNameContaining(
            sampleProject.getSupervisorName().getSupervisorName());
    assertEquals(1, projects.size());
  }

  @Test
  void when_project_find_by_supervisor_name_containing_partial_supervisor_name_then_found() {
    saveSampleProject();

    // Get a random substring from supervisor name
    String fullSupervisorName = sampleProject.getSupervisorName().getSupervisorName();
    String partialSupervisorName =
        sampleProject
            .getSupervisorName()
            .getSupervisorName()
            .substring(1, fullSupervisorName.length() - 1);

    // Verify substring is a real substring of supervisor name
    assertTrue(fullSupervisorName.contains(partialSupervisorName));

    Set<Project> projects =
        projectRepository.findBySupervisorName_SupervisorNameContaining(partialSupervisorName);
    assertEquals(1, projects.size());
  }

  @Test
  void when_project_find_by_supervisor_name_containing_invalid_supervisor_name_then_not_found() {
    saveSampleProject();

    Set<Project> projects =
        projectRepository.findBySupervisorName_SupervisorNameContaining(
            UUID.randomUUID().toString());
    assertEquals(0, projects.size());
  }
}
