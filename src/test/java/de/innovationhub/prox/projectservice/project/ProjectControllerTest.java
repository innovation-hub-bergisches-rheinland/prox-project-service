package de.innovationhub.prox.projectservice.project;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.user.User;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("h2")
@SuppressWarnings("java:S2699")
class ProjectControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired EntityManager entityManager;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.mockMvc(mockMvc);
  }

  @Test
  void shouldGetProjects() {
    var easyRandom = new EasyRandom();
    var randomProjects =
        List.of(
            getTestProject(),
            getTestProject(),
            getTestProject(),
            getTestProject(),
            getTestProject());

    randomProjects.forEach(randomProject -> entityManager.persist(randomProject));
    entityManager.flush();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/projects")
        .then()
        .status(HttpStatus.OK)
        .body("projects", hasSize(5));
    // @formatter:on
  }

  @Test
  void shouldGetProject() {
    var easyRandom = new EasyRandom();
    var randomProject = getTestProject();
    this.entityManager.persist(randomProject);
    entityManager.flush();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/projects/{id}", randomProject.getId())
        .then()
        .status(HttpStatus.OK);
    // @formatter:on
  }

  @Test
  void shouldReturnUnauthorized() {
    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/users/35982f30-18df-48bf-afc1-e7f8deeeb49c/projects")
        .then()
        .status(HttpStatus.UNAUTHORIZED);
    // @formatter:on
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldCreateProjectForAuthenticatedUser() {
    var easyRandom = new EasyRandom();
    var randomProject = getTestProject();
    var user = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"), "Xavier Tester");
    entityManager.persist(user);

    // @formatter:off
    var id =
        given()
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(randomProject)
            .when()
            .post("user/projects")
            .then()
            .status(HttpStatus.CREATED)
            .extract()
            .body()
            .jsonPath()
            .getUUID("id");
    // @formatter:on

    var project = this.entityManager.find(Project.class, id);
    assertThat(project).isNotNull();
    assertThat(project.getOwner().getId())
        .isEqualTo(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldCreateProject() {
    var easyRandom = new EasyRandom();
    var randomProject = getTestProject();
    var user = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"), "Xavier Tester");
    entityManager.persist(user);

    // @formatter:off
    var id =
        given()
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(randomProject)
            .when()
            .post("users/35982f30-18df-48bf-afc1-e7f8deeeb49c/projects")
            .then()
            .status(HttpStatus.CREATED)
            .extract()
            .body()
            .jsonPath()
            .getUUID("id");
    // @formatter:on

    var project = this.entityManager.find(Project.class, id);
    assertThat(project).isNotNull();
    assertThat(project.getOwner().getId())
        .isEqualTo(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldUpdateProject() {
    var easyRandom = new EasyRandom();
    // Ensure that authenticated User is the creator
    var owner = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"), "Xavier Tester");
    var randomProject = getTestProject(owner);

    this.entityManager.persist(randomProject);
    entityManager.flush();

    var updatedProject = getTestProject();
    updatedProject.setOwner(owner);

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(updatedProject)
        .when()
        .put("/projects/{id}", randomProject.getId())
        .then()
        .status(HttpStatus.OK);
    // @formatter:on

    var found = this.entityManager.find(Project.class, randomProject.getId());
    assertThat(found).isNotNull();
    assertThat(found)
        .usingRecursiveComparison()
        .ignoringFields("id", "createdAt", "creatorName", "modifiedAt", "modules")
        .isEqualTo(updatedProject);
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  @Disabled("Not implemented")
  void shouldPartiallyUpdateProject() {
    var easyRandom = new EasyRandom();
    // Ensure that authenticated User is the creator
    var owner = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"), "Xavier Tester");
    var randomProject = getTestProject(owner);

    this.entityManager.persist(randomProject);
    entityManager.flush();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body("""
            {
              "name": "Test 123"
            }
            """)
        .when()
        .patch("/projects/{id}", randomProject.getId())
        .then()
        .status(HttpStatus.OK);
    // @formatter:on

    var found = this.entityManager.find(Project.class, randomProject.getId());
    assertThat(found).isNotNull();
    assertThat(found.getName()).isEqualTo("Test 123");
    // Everything except name is unchanged
    assertThat(found).usingRecursiveComparison().ignoringFields("name").isEqualTo(randomProject);
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldDeleteProject() {
    var easyRandom = new EasyRandom();
    // Ensure that authenticated User is the creator
    var owner = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"), "Xavier Tester");
    var randomProject = getTestProject(owner);

    this.entityManager.persist(randomProject);
    entityManager.flush();

    assertThat(this.entityManager.find(Project.class, randomProject.getId())).isNotNull();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .delete("/projects/{id}", randomProject.getId())
        .then()
        .status(HttpStatus.NO_CONTENT);
    // @formatter:on

    assertThat(this.entityManager.find(Project.class, randomProject.getId())).isNull();
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldUpdateModulesOfProject() {
    var easyRandom = new EasyRandom();
    var randomModules =
        List.of(new ModuleType("AB", "Alpha Beta"), new ModuleType("BG", "Beta Gamma"));
    // Ensure that authenticated User is the creator
    var owner = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"), "Xavier Tester");
    var randomProject = getTestProject(owner);

    this.entityManager.persist(randomProject);
    randomModules.forEach(moduleType -> this.entityManager.persist(moduleType));
    this.entityManager.flush();

    var moduleKeys = randomModules.stream().map(ModuleType::getKey).toList();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(moduleKeys)
        .when()
        .put("/projects/{id}/modules", randomProject.getId())
        .then()
        .status(HttpStatus.OK);
    // @formatter:on

    var foundProject = this.entityManager.find(Project.class, randomProject.getId());
    assertThat(foundProject).isNotNull();
    assertThat(foundProject.getModules()).containsExactlyInAnyOrderElementsOf(randomModules);
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldUpdateSpecializationOfProject() {
    var easyRandom = new EasyRandom();
    var randomSpecializations =
        List.of(new Specialization("AB", "Alpha Beta"), new Specialization("BG", "Beta Gamma"));
    // Ensure that authenticated User is the creator
    var owner = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"), "Xavier Tester");
    var randomProject = getTestProject(owner);

    this.entityManager.persist(randomProject);
    randomSpecializations.forEach(specialization -> this.entityManager.persist(specialization));
    this.entityManager.flush();

    var specializationIds =
        randomSpecializations.stream().map(specialization -> specialization.getKey()).toList();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(specializationIds)
        .when()
        .put("/projects/{id}/specializations", randomProject.getId())
        .then()
        .status(HttpStatus.OK);
    // @formatter:on

    var foundProject = this.entityManager.find(Project.class, randomProject.getId());
    assertThat(foundProject).isNotNull();
    assertThat(foundProject.getSpecializations())
        .containsExactlyInAnyOrderElementsOf(randomSpecializations);
  }

  private Project getTestProject() {
    var owner = new User(UUID.randomUUID(), "Xavier Tester");
    return getTestProject(owner);
  }

  private Project getTestProject(User owner) {
    this.entityManager.persist(owner);
    return new Project(
        "Test Project",
        "Test Project Description",
        "Test Project Short Description",
        "Test Project Requirement",
        ProjectStatus.AVAILABLE,
        "Test Project Creator Name",
        getSupervisors(),
        Collections.emptySet(),
        Collections.emptySet(),
        owner,
        Instant.now(),
        Instant.now());
  }

  // Note: We need to construct a mutable collection for ElementCollections (thanks Hibernate)
  private List<Supervisor> getSupervisors() {
    var list = new ArrayList<Supervisor>();
    list.add(new Supervisor(UUID.randomUUID(), "Test Project Supervisor"));
    return list;
  }
}
