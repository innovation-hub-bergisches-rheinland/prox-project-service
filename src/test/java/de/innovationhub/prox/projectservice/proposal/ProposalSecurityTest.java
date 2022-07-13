package de.innovationhub.prox.projectservice.proposal;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import de.innovationhub.prox.projectservice.project.Supervisor;
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
import org.springframework.test.web.servlet.MockMvc;

// TODO: This is just a quick test. We should implement a proper test suite for integration
//  tests.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("java:S2699")
class ProposalSecurityTest {

  @Autowired MockMvc mockMvc;

  @Autowired EntityManager entityManager;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.mockMvc(mockMvc);
  }

  @Test
  void shouldReturnUnauthorizedPost() {
    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/users/35982f30-18df-48bf-afc1-e7f8deeeb49c/proposals")
        .then()
        .status(HttpStatus.UNAUTHORIZED);
    // @formatter:on
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldCreateProposalForUser() {
    // @formatter:off
    var id =
        given()
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("""
            {
              "name": "Test",
              "description": "Test",
              "shortDescription": "Test",
              "requirement": "Test"
            }
            """)
            .when()
            .post("/users/35982f30-18df-48bf-afc1-e7f8deeeb49c/proposals")
            .then()
            .status(HttpStatus.CREATED);
    // @formatter:on
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldUpdate() {
    // Ensure that authenticated User is the creator
    var owner = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
    var proposal = getTestProposal(owner);

    this.entityManager.persist(proposal);
    entityManager.flush();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body("""
            {
              "name": "Test",
              "description": "Test",
              "shortDescription": "Test",
              "requirement": "Test"
            }
            """)
        .when()
        .put("/proposals/{id}", proposal.getId())
        .then()
        .status(HttpStatus.OK);
    // @formatter:on
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldDelete() {
    // Ensure that authenticated User is the creator
    var owner = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
    var proposal = getTestProposal(owner);

    this.entityManager.persist(proposal);
    entityManager.flush();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .delete("/proposals/{id}", proposal.getId())
        .then()
        .status(HttpStatus.NO_CONTENT);
    // @formatter:on
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldUpdateModules() {
    var easyRandom = new EasyRandom();
    var randomModules =
        List.of(new ModuleType("AB", "Alpha Beta"), new ModuleType("BG", "Beta Gamma"));
    // Ensure that authenticated User is the creator
    var owner = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
    var proposal = getTestProposal(owner);

    this.entityManager.persist(proposal);
    randomModules.forEach(moduleType -> this.entityManager.persist(moduleType));
    this.entityManager.flush();

    var moduleKeys = randomModules.stream().map(ModuleType::getKey).toList();

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(moduleKeys)
        .when()
        .put("/proposals/{id}/modules", proposal.getId())
        .then()
        .status(HttpStatus.OK);
    // @formatter:on
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c"))
  void shouldUpdateSpecialization() {
    var easyRandom = new EasyRandom();
    var randomSpecializations =
        List.of(new Specialization("AB", "Alpha Beta"), new Specialization("BG", "Beta Gamma"));
    // Ensure that authenticated User is the creator
    var owner = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
    var testProposal = getTestProposal(owner);

    this.entityManager.persist(testProposal);
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
        .put("/proposals/{id}/specializations", testProposal.getId())
        .then()
        .status(HttpStatus.OK);
    // @formatter:on
  }

  private Proposal getTestProposal(User owner) {
    this.entityManager.persist(owner);
    return Proposal.builder()
        .name("Test")
        .owner(owner)
        .shortDescription("Test")
        .requirement("Test")
        .description("Test")
        .build();
  }

  // Note: We need to construct a mutable collection for ElementCollections (thanks Hibernate)
  private List<Supervisor> getSupervisors() {
    var list = new ArrayList<Supervisor>();
    list.add(new Supervisor(UUID.randomUUID(), "Test Project Supervisor"));
    return list;
  }
}
