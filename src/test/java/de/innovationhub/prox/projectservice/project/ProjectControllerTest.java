package de.innovationhub.prox.projectservice.project;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.keycloak.WithMockKeycloakAuth;
import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
    var randomProjects = easyRandom.objects(Project.class, 5).toList();

    randomProjects.forEach(randomProject -> entityManager.persist(randomProject));
    entityManager.flush();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/projects")
        .then()
        .status(HttpStatus.OK)
        .body("_embedded.projects", hasSize(5));
    // @formatter:on
  }

  @Test
  void shouldGetProject() {
    var easyRandom = new EasyRandom();
    var randomProject = easyRandom.nextObject(Project.class);
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
        .post("/projects")
        .then()
        .status(HttpStatus.UNAUTHORIZED);
    // @formatter:on
  }

  @Test
  @WithMockKeycloakAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldCreateProject() {
    var easyRandom = new EasyRandom();
    var randomProject = easyRandom.nextObject(Project.class);

    // @formatter:off
    var id =
        given()
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(randomProject)
            .when()
            .post("/projects")
            .then()
            .status(HttpStatus.CREATED)
            .extract()
            .body()
            .jsonPath()
            .getUUID("id");
    // @formatter:on

    var project = this.entityManager.find(Project.class, id);
    assertThat(project).isNotNull();
    assertThat(project.getCreatorID())
        .isEqualTo(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
  }

  @Test
  @WithMockKeycloakAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldUpdateProject() {
    var easyRandom = new EasyRandom();
    var randomProject = easyRandom.nextObject(Project.class);

    // Ensure that authenticated User is the creator
    randomProject.setCreatorID(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
    this.entityManager.persist(randomProject);
    entityManager.flush();

    var updatedProject = easyRandom.nextObject(Project.class);

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
        .ignoringFields("id", "createdAt", "creatorName", "modifiedAt", "creatorID", "modules")
        .isEqualTo(updatedProject);
  }

  @Test
  @WithMockKeycloakAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldPartiallyUpdateProject() {
    var easyRandom = new EasyRandom();
    var randomProject = easyRandom.nextObject(Project.class);

    // Ensure that authenticated User is the creator
    randomProject.setCreatorID(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
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
  @WithMockKeycloakAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldDeleteProject() {
    var easyRandom = new EasyRandom();
    var randomProject = easyRandom.nextObject(Project.class);

    // Ensure that authenticated User is the creator
    randomProject.setCreatorID(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
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
  @WithMockKeycloakAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldUpdateModulesOfProject() {
    var easyRandom = new EasyRandom();
    var randomModules = easyRandom.objects(ModuleType.class, 2).collect(Collectors.toList());
    var randomProject = easyRandom.nextObject(Project.class);

    // Ensure that authenticated User is the creator
    randomProject.setCreatorID(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
    this.entityManager.persist(randomProject);
    entityManager.flush();

    randomModules.forEach(moduleType -> this.entityManager.persist(moduleType));

    var moduleIds =
        randomModules.stream().map(moduleType -> moduleType.getId().toString()).toList();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType("text/uri-list")
        .body(String.join("\n", moduleIds))
        .when()
        .put("/projects/{id}/modules", randomProject.getId())
        .then()
        .status(HttpStatus.NO_CONTENT);
    // @formatter:on

    var foundProject = this.entityManager.find(Project.class, randomProject.getId());
    assertThat(foundProject).isNotNull();
    assertThat(foundProject.getModules()).containsExactlyInAnyOrderElementsOf(randomModules);
  }

  @Test
  @WithMockKeycloakAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldUpdateSpecializationOfProject() {
    var easyRandom = new EasyRandom();
    var randomSpecializations =
        easyRandom.objects(Specialization.class, 2).collect(Collectors.toList());
    var randomProject = easyRandom.nextObject(Project.class);

    // Ensure that authenticated User is the creator
    randomProject.setCreatorID(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
    this.entityManager.persist(randomProject);
    entityManager.flush();

    randomSpecializations.forEach(specialization -> this.entityManager.persist(specialization));

    var specializationIds =
        randomSpecializations.stream()
            .map(specialization -> specialization.getId().toString())
            .toList();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType("text/uri-list")
        .body(String.join("\n", specializationIds))
        .when()
        .put("/projects/{id}/specializations", randomProject.getId())
        .then()
        .status(HttpStatus.NO_CONTENT);
    // @formatter:on

    var foundProject = this.entityManager.find(Project.class, randomProject.getId());
    assertThat(foundProject).isNotNull();
    assertThat(foundProject.getSpecializations())
        .containsExactlyInAnyOrderElementsOf(randomSpecializations);
  }
}
