package de.innovationhub.prox.projectservice.project;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.user.User;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("testcontainers-postgres")
@Transactional
class ProjectRepositoryCustomTest {

  @Autowired
  ProjectRepository projectRepository;

  @Autowired
  EntityManager em;

  @Container
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.1")
      .withDatabaseName("project-db")
      .withUsername("project-service")
      .withPassword("project-service");

  @DynamicPropertySource
  static void setupDB(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
  }

  @Test
  void shouldFindBySingleTag() {
    var project = getTestProject();
    project.setTags(Collections.singletonList("tag"));
    projectRepository.save(project);

    var projects = projectRepository.search("tag");

    assertEquals(1, projects.size());
  }

  @Test
  void shouldFindByMultipleTags() {
    var project = getTestProject();
    project.setTags(List.of("tag1", "tag2"));
    projectRepository.save(project);

    var projects = projectRepository.search("tag1 tag2");

    assertEquals(1, projects.size());
  }

  @Test
  void shouldFindByName() {
    var project = getTestProject();
    projectRepository.save(project);

    var projects = projectRepository.search(project.getName());

    assertEquals(1, projects.size());
  }

  @Test
  void shouldFindByShortDescription() {
    var project = getTestProject();
    projectRepository.save(project);

    var projects = projectRepository.search("Short Description");

    assertEquals(1, projects.size());
  }

  @Test
  void shouldFindByPartialName() {
    var project = getTestProject();
    project.setName("Lorem Ipsum");
    projectRepository.save(project);

    var projects = projectRepository.search("Lorem");

    assertEquals(1, projects.size());
  }

  @Test
  void shouldFindByNameCaseInsensitive() {
    var project = getTestProject();
    project.setName("Lorem Ipsum");
    projectRepository.save(project);

    var projects = projectRepository.search("lorem ipsum");

    assertEquals(1, projects.size());
  }

  @Test
  void shouldFindByNameAndTag() {
    var project = getTestProject();
    project.setName("Lorem Ipsum");
    project.setTags(List.of("tag1", "tag2"));
    projectRepository.save(project);

    var projects = projectRepository.search("Lorem Ipsum tag1");

    assertEquals(1, projects.size());
  }

  @Test
  void shouldFindFuzzyByNameAndTag() {
    var project = getTestProject();
    project.setName("Lorem Ipsum");
    project.setTags(List.of("tag1", "tag2"));
    projectRepository.save(project);

    var projects = projectRepository.search("Ipsum Lorme tag");

    assertEquals(1, projects.size());
  }

  @Test
  void shouldFindByStatus() {
    var project = getTestProject();
    project.setName("Lorem Ipsum");
    project.setTags(List.of("tag1", "tag2"));
    project.setStatus(ProjectStatus.FINISHED);
    projectRepository.save(project);

    var projects = projectRepository.filterProjects(ProjectStatus.FINISHED, null, null, null);

    assertEquals(1, projects.size());
  }

  @Test
  void shouldFindBySpecialization() {
    var project = getTestProject();
    projectRepository.save(project);

    var projects = projectRepository.filterProjects(null, List.of("s1"), null, null);

    assertEquals(1, projects.size());
  }

  @Test
  void shouldNotFindBySpecialization() {
    var project = getTestProject();
    projectRepository.save(project);

    var projects = projectRepository.filterProjects(null, List.of("UNKNOWN"), null, null);

    assertEquals(0, projects.size());
  }

  @Test
  void shouldFindByModuleType() {
    var project = getTestProject();
    projectRepository.save(project);

    var projects = projectRepository.filterProjects(null, null, List.of("m1"), null);

    assertEquals(1, projects.size());
  }

  @Test
  void shouldNotFindByModuleType() {
    var project = getTestProject();
    projectRepository.save(project);

    var projects = projectRepository.filterProjects(null, null, List.of("UNKNOWN"), null);

    assertEquals(0, projects.size());
  }

  @Test
  void shouldFindByModuleTypeAndSpecialization() {
    var project = getTestProject();
    projectRepository.save(project);

    var projects = projectRepository.filterProjects(null, List.of("s1"), List.of("m1"), null);

    assertEquals(1, projects.size());
  }

  @Test
  void shouldFindIfEverythingNull() {
    var project = getTestProject();
    projectRepository.save(project);

    var projects = projectRepository.filterProjects(null , null, null, null);

    assertEquals(1, projects.size());
  }

  @Test
  void shouldFindIfEverythingDefault() {
    var project = getTestProject();
    projectRepository.save(project);

    var projects = projectRepository.filterProjects(null, List.of(), List.of(), "");

    assertEquals(1, projects.size());
  }

  private Project getTestProject() {
    var owner = new User(UUID.randomUUID(), "Xavier Tester");
    em.persist(owner);
    Specialization s1 = new Specialization("s1", "Specialization 1");
    Specialization s2 = new Specialization("s2", "Specialization 2");
    em.persist(s1);
    em.persist(s2);

    ModuleType m1 = new ModuleType("m1", "Module 1");
    ModuleType m2 = new ModuleType("m2", "Module 2");
    em.persist(m1);
    em.persist(m2);

    return new Project(
      "Test Project",
      "Test Project Description",
      "Test Project Short Description",
      "Test Project Requirement",
      ProjectStatus.AVAILABLE,
      "Test Project Creator Name",
      List.of(),
      Set.of(s1, s2),
      Set.of(m1, m2),
      owner,
      null,
      Collections.emptyList(),
      Instant.now(),
      Instant.now());
  }
}
