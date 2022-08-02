package de.innovationhub.prox.projectservice.proposal;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.assertj.core.api.SoftAssertions;
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
class ProposalIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  EntityManager entityManager;

  @Autowired
  Validator validator;

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
              "requirement": "Test"
            }
            """)
            .when()
            .post("/users/35982f30-18df-48bf-afc1-e7f8deeeb49c/proposals")
            .then()
            .status(HttpStatus.CREATED)
            .extract()
            .jsonPath()
            .getUUID("id");
    // @formatter:on

    var proposal = entityManager.find(Proposal.class, id);
    var proposalAssertions = new SoftAssertions();
    proposalAssertions.assertThat(proposal)
        .extracting(Proposal::getName, Proposal::getDescription,
            Proposal::getRequirement, Proposal::getStatus)
        .doesNotContainNull()
        .containsExactly("Test", "Test", "Test", ProposalStatus.PROPOSED);
    proposalAssertions.assertThat(proposal)
        .extracting(p -> p.getOwner().getId(), p -> p.getOwner().getDiscriminator())
        .containsExactly(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"), "user");
    proposalAssertions.assertAll();

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
              "name": "Test2",
              "description": "Test2",
              "requirement": "Test2"
            }
            """)
        .when()
        .put("/proposals/{id}", proposal.getId())
        .then()
        .status(HttpStatus.OK);
    // @formatter:on

    var result = this.entityManager.find(Proposal.class, proposal.getId());

    var proposalAssertions = new SoftAssertions();
    proposalAssertions.assertThat(result)
        .extracting(Proposal::getName, Proposal::getDescription,
            Proposal::getRequirement, Proposal::getStatus)
        .doesNotContainNull()
        .containsExactly("Test2", "Test2", "Test2", ProposalStatus.PROPOSED);
    proposalAssertions.assertThat(result)
        .extracting(p -> p.getOwner().getId(), p -> p.getOwner().getDiscriminator())
        .containsExactly(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"), "user");
    proposalAssertions.assertAll();
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
    var result = this.entityManager.find(Proposal.class, proposal.getId());
    assertThat(result)
        .isNull();
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

    var result = this.entityManager.find(Proposal.class, proposal.getId());
    assertThat(result.getModules())
        .containsExactlyInAnyOrderElementsOf(randomModules);
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

    var result = this.entityManager.find(Proposal.class, testProposal.getId());
    assertThat(result.getSpecializations())
        .containsExactlyInAnyOrderElementsOf(randomSpecializations);
  }

  @Test
  @WithMockJwtAuth(
      authorities = {"ROLE_professor"},
      claims = @OpenIdClaims(sub = "35982f30-18df-48bf-afc1-e7f8deeeb49c", name = "Karl Peter"))
  void shouldApplyCommitment() {
    var easyRandom = new EasyRandom();
    var randomSpecializations =
        List.of(new Specialization("AB", "Alpha Beta"), new Specialization("BG", "Beta Gamma"));
    // Ensure that authenticated User is the creator
    var owner = new User(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"));
    var testProposal = getTestProposal(owner);

    randomSpecializations.forEach(specialization -> this.entityManager.persist(specialization));
    testProposal.setSpecializations(randomSpecializations.stream().collect(Collectors.toSet()));
    this.entityManager.persist(testProposal);
    this.entityManager.flush();

    // @formatter:off
    var projectId = given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .post("/proposals/{id}/commitment", testProposal.getId())
        .then()
        .status(HttpStatus.CREATED)
        .extract()
        .jsonPath()
        .getUUID("id");

    // @formatter:on

    var result = this.entityManager.find(Proposal.class, testProposal.getId());
    assertThat(result).isNull();
    var projectResult = this.entityManager.find(Project.class, projectId);
    assertThat(projectResult).isNotNull();
    assertThat(projectResult.getSupervisors())
        .extracting("id", "name")
        .contains(tuple(UUID.fromString("35982f30-18df-48bf-afc1-e7f8deeeb49c"), "Karl Peter"));
  }

  private Proposal getTestProposal(User owner) {
    this.entityManager.persist(owner);
    return Proposal.builder()
        .name("Test")
        .owner(owner)
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