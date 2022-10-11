package de.innovationhub.prox.projectservice.proposal;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.project.Supervisor;
import de.innovationhub.prox.projectservice.project.event.ProposalPromotedToProject;
import de.innovationhub.prox.projectservice.proposal.dto.CreateProposalDto;
import de.innovationhub.prox.projectservice.proposal.event.ProposalChanged;
import de.innovationhub.prox.projectservice.proposal.event.ProposalDeleted;
import de.innovationhub.prox.projectservice.proposal.event.ProposalReceivedCommitment;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Validator;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("h2")
@RecordApplicationEvents
@SuppressWarnings("java:S2699")
class ProposalControllerIT {
  private final static String USER_ID_STR = "35982f30-18df-48bf-afc1-e7f8deeeb49c";
  private final static UUID USER_ID = UUID.fromString(USER_ID_STR);

  @Autowired
  MockMvc mockMvc;

  @Autowired
  EntityManager entityManager;

  @Autowired
  Validator validator;

  @Autowired
  ApplicationEvents applicationEvents;

  // TODO: Add testcontainers instead of mocking.
  @MockBean
  KafkaTemplate<String, Object> kafkaTemplate;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.mockMvc(mockMvc);
  }

  @Nested
  class SecurityTests {
    @Test
    void shouldReturnUnauthorizedPost() {
      given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/users/35982f30-18df-48bf-afc1-e7f8deeeb49c/proposals")
        .then()
        .status(HttpStatus.UNAUTHORIZED);
    }
  }

  @Test
  @WithMockJwtAuth(
    authorities = {"ROLE_professor"},
    claims = @OpenIdClaims(sub = USER_ID_STR))
  void shouldCreateProposalForUser() throws InterruptedException {
    var user = createAndPersistUser(USER_ID);
    var proposalToCreate = getTestProposalDto();

    // @formatter:off
    var id =
        given()
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(proposalToCreate)
            .when()
            .post("/users/{userId}/proposals", USER_ID_STR)
            .then()
            .status(HttpStatus.CREATED)
            .extract()
            .jsonPath()
            .getUUID("id");
    // @formatter:on

    assertProposalExists(id);
    assertProposalIsOwnedByUser(id, USER_ID);
  }

  @Test
  @WithMockJwtAuth(
    authorities = {"ROLE_professor"},
    claims = @OpenIdClaims(sub = USER_ID_STR))
  void shouldUpdate() throws InterruptedException {
    // Ensure that authenticated User is the creator
    var owner = createAndPersistUser(UUID.fromString(USER_ID_STR));
    var proposal = getTestProposal(owner);

    this.entityManager.persist(proposal);

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(
            """
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
    proposalAssertions
      .assertThat(result)
      .extracting(
        Proposal::getName,
        Proposal::getDescription,
        Proposal::getRequirement,
        Proposal::getStatus)
      .doesNotContainNull()
      .containsExactly("Test2", "Test2", "Test2", ProposalStatus.PROPOSED);
    proposalAssertions
      .assertThat(result.getOwnerId())
      .isEqualTo(UUID.fromString(USER_ID_STR));
    proposalAssertions.assertAll();

    assertThat(applicationEvents.stream(ProposalChanged.class))
      .hasSize(1);
  }

  @Test
  @WithMockJwtAuth(
    authorities = {"ROLE_professor"},
    claims = @OpenIdClaims(sub = USER_ID_STR))
  void shouldDelete() throws InterruptedException {
    var owner = createAndPersistUser(UUID.fromString(USER_ID_STR));
    var proposal = getTestProposal(owner);

    this.entityManager.persist(proposal);

    // @formatter:off
    given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .delete("/proposals/{id}", proposal.getId())
        .then()
        .status(HttpStatus.NO_CONTENT);
    // @formatter:on
    var result = this.entityManager.find(Proposal.class, proposal.getId());
    assertThat(result).isNull();

    assertThat(applicationEvents.stream(ProposalDeleted.class))
      .hasSize(1);
  }

  @Test
  @WithMockJwtAuth(
    authorities = {"ROLE_professor"},
    claims = @OpenIdClaims(sub = USER_ID_STR))
  void shouldUpdateModules() throws InterruptedException {
    var randomModules =
      List.of(new ModuleType("AB", "Alpha Beta"), new ModuleType("BG", "Beta Gamma"));
    // Ensure that authenticated User is the creator
    var owner = createAndPersistUser(UUID.fromString(USER_ID_STR));
    var proposal = getTestProposal(owner);

    this.entityManager.persist(proposal);
    randomModules.forEach(moduleType -> this.entityManager.persist(moduleType));

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
    assertThat(result.getModules()).containsExactlyInAnyOrderElementsOf(randomModules);

    assertThat(applicationEvents.stream(ProposalChanged.class))
      .hasSize(1);
  }

  @Test
  @WithMockJwtAuth(
    authorities = {"ROLE_professor"},
    claims = @OpenIdClaims(sub = USER_ID_STR))
  void shouldUpdateSpecialization() throws InterruptedException {
    var randomSpecializations =
      List.of(new Specialization("AB", "Alpha Beta"), new Specialization("BG", "Beta Gamma"));
    var owner = createAndPersistUser(UUID.fromString(USER_ID_STR));
    var testProposal = getTestProposal(owner);

    this.entityManager.persist(testProposal);
    randomSpecializations.forEach(specialization -> this.entityManager.persist(specialization));

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

    assertThat(applicationEvents.stream(ProposalChanged.class))
      .hasSize(1);
  }

  @Test
  @WithMockJwtAuth(
    authorities = {"ROLE_professor"},
    claims = @OpenIdClaims(sub = USER_ID_STR, name = "Karl Peter"))
  void shouldApplyCommitment() throws InterruptedException {
    var randomSpecializations =
      List.of(new Specialization("AB", "Alpha Beta"), new Specialization("BG", "Beta Gamma"));
    // Ensure that authenticated User is the creator
    var owner = createAndPersistUser(UUID.fromString(USER_ID_STR));
    var testProposal = getTestProposal(owner);

    randomSpecializations.forEach(specialization -> this.entityManager.persist(specialization));
    testProposal.setSpecializations(randomSpecializations.stream().collect(Collectors.toSet()));
    this.entityManager.persist(testProposal);

    // @formatter:off
    var projectId =
        given()
            .accept(MediaType.APPLICATION_JSON)
            .when()
            .post("/proposals/{id}/commitment", testProposal.getId())
            .then()
            .status(HttpStatus.OK)
            .extract()
            .jsonPath()
            .getUUID("id");

    // @formatter:on

    var result = this.entityManager.find(Proposal.class, testProposal.getId());
    assertThat(result)
      .isNotNull()
        .satisfies(proposal -> {
          assertThat(result.getCommittedSupervisor()).isEqualTo(UUID.fromString(USER_ID_STR));
          assertThat(result.getStatus()).isEqualTo(ProposalStatus.HAS_COMMITMENT);
        });

    assertThat(applicationEvents.stream(ProposalChanged.class))
      .hasSize(1);
    assertThat(applicationEvents.stream(ProposalReceivedCommitment.class))
      .hasSize(1);
    assertThat(applicationEvents.stream(ProposalPromotedToProject.class))
      .hasSize(1);
  }

  private Proposal getTestProposal(User owner) {
    this.entityManager.persist(owner);
    return Proposal.builder()
        .name("Test")
        .ownerId(owner.getId())
        .requirement("Test")
        .description("Test")
        .build();
  }

  private CreateProposalDto getTestProposalDto() {
    return new CreateProposalDto("Test", "Test", "Test");
  }

  private void assertProposalExists(UUID uuid) {
    assertThat(this.entityManager.find(Proposal.class, uuid)).isNotNull();
  }

  private void assertProposalIsOwnedByUser(UUID uuid, UUID userId) {
    assertThat(this.entityManager.find(Proposal.class, uuid))
      .isNotNull()
      .satisfies(proposal -> {
        assertThat(proposal.getOwnerId()).isEqualTo(userId);
      });
  }

  // Note: We need to construct a mutable collection for ElementCollections (thanks Hibernate)
  private List<Supervisor> getSupervisors() {
    var list = new ArrayList<Supervisor>();
    list.add(new Supervisor(UUID.randomUUID(), "Test Project Supervisor"));
    return list;
  }

  private User createAndPersistUser(UUID userId) {
    var user = new User(userId, "Test User");
    this.entityManager.persist(user);
    return user;
  }
}
