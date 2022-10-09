package de.innovationhub.prox.projectservice.owner;

import static de.innovationhub.prox.projectservice.AwaitilityAssertions.assertEventually;
import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.AbstractRedpandaIT;
import de.innovationhub.prox.projectservice.owners.OrganizationEntityEventDto;
import de.innovationhub.prox.projectservice.owners.OrganizationEntityEventDto.Membership;
import de.innovationhub.prox.projectservice.owners.UserEntityEventDto;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2")
class OwnerEntityListenerIT extends AbstractRedpandaIT {

  @Autowired
  KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  UserRepository userRepository;

  @Autowired
  OrganizationRepository organizationRepository;

  static final String USER_TOPIC = "entity.user.user";
  static final String ORGANIZATION_TOPIC = "entity.organization.organization";

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
    organizationRepository.deleteAll();
  }

  @Test
  void shouldCreateUserOnUserEvent() throws Exception {
    var userId = UUID.randomUUID();
    var userName = "John Doe";
    var event = createUserEventDto(userId, userName);

    var ft = kafkaTemplate
      .send(USER_TOPIC, userId.toString(), event);
    ft.get(5, TimeUnit.SECONDS);

    shouldEventuallyCreateUserWithName(userId, userName);
  }

  @Test
  void shouldUpdateUserOnUserEvent() throws Exception {
    var userId = UUID.randomUUID();
    var userName = "John Doe";
    var user = createAndPersistUser(userId, userName);
    var updateEvent = createUserEventDto(userId, "Jane Doe");

    var ft = kafkaTemplate.send(USER_TOPIC, userId.toString(), updateEvent);
    ft.get(5, TimeUnit.SECONDS);

    shouldEventuallyCreateUserWithName(userId, "Jane Doe");
  }

  @Test
  void shouldDeleteUserOnNullEvent() throws Exception {
    var userId = UUID.randomUUID();
    createAndPersistUser(userId);

    // When
    var ft = kafkaTemplate.send(USER_TOPIC, userId.toString(), null);
    ft.get(5, TimeUnit.SECONDS);

    // Then
    shouldEventuallyDeleteUserWithId(userId);
  }

  @Test
  void shouldCreateOrganizationOnOrganizationEvent() throws Exception {
    // Given
    var orgId = UUID.randomUUID();
    var orgName = "ACME Ltd.";
    var event = createOrganizationEventDto(orgId, orgName);

    // When
    var ft = kafkaTemplate.send(ORGANIZATION_TOPIC, orgId.toString(), event);
    ft.get(5, TimeUnit.SECONDS);

    // Then
    assertEventually(() ->
        assertThat(organizationRepository.findById(orgId))
          .isPresent()
          .get()
          .satisfies(o -> {
            assertThat(o.getName()).isEqualTo(orgName);
            assertThat(o.getMembers()).isEmpty();
          })
      );
  }

  @Test
  void shouldUpdateOrganizationOnOrganizationEvent() throws Exception {
    // Given
    var orgId = UUID.randomUUID();
    var orgName = "ACME Ltd.";
    createAndPersistOrganization(orgId, orgName);
    var event = createOrganizationEventDto(orgId, "ACME Inc.");

    // When
    var ft = kafkaTemplate.send(ORGANIZATION_TOPIC, orgId.toString(), event);
    ft.get(5, TimeUnit.SECONDS);

    // Then
    assertEventually(() ->
        assertThat(organizationRepository.findById(orgId))
          .isPresent()
          .get()
          .satisfies(organization -> {
            assertThat(organization.getName())
              .isEqualTo("ACME Inc.");
          })
      );
  }

  @Test
  void shouldUpdateMembershipsOnOrganizationEvent() throws Exception {
    // Given
    var orgId = UUID.randomUUID();
    var members = List.of(UUID.randomUUID());
    createAndPersistOrganization(orgId, members);
    var event = createOrganizationEventDto(orgId, "ACME Inc.", members);

    // When
    var ft = kafkaTemplate.send(ORGANIZATION_TOPIC, orgId.toString(), event);
    ft.get(5, TimeUnit.SECONDS);

    // Then
    assertEventually(() ->
      assertThat(organizationRepository.findById(orgId))
        .isPresent()
        .get()
        .satisfies(organization -> {
          assertThat(organization.getMembers())
            .containsExactlyInAnyOrderElementsOf(members);
        })
    );
  }

  @Test
  void shouldDeleteOrganizationOnNulLEvent() throws Exception {
    // Given
    var orgId = UUID.randomUUID();
    createAndPersistOrganization(orgId);

    // When
    var ft = kafkaTemplate.send(ORGANIZATION_TOPIC, orgId.toString(), null);
    ft.get(5, TimeUnit.SECONDS);

    shouldEventuallyDeleteOrganizationWithId(orgId);
  }

  private void shouldEventuallyCreateUserWithName(UUID userId, String name) {
    assertEventually(() ->
        assertThat(userRepository.findById(userId))
          .isPresent()
          .get()
          .extracting(User::getName)
          .isEqualTo(name)
      );
  }

  private void shouldEventuallyDeleteUserWithId(UUID userId) {
    assertEventually(() ->
        assertThat(userRepository.existsById(userId))
          .isFalse()
      );
  }

  private void shouldEventuallyDeleteOrganizationWithId(UUID organizationId) {
    assertEventually(() ->
        assertThat(organizationRepository.existsById(organizationId))
          .isFalse()
    );
  }

  private OrganizationEntityEventDto createOrganizationEventDto(UUID orgId, String orgName) {
    return createOrganizationEventDto(orgId, orgName, List.of());
  }

  private OrganizationEntityEventDto createOrganizationEventDto(UUID orgId, String orgName, List<UUID> members) {
    Map<UUID, OrganizationEntityEventDto.Membership> memberMap = members.stream()
      .collect(Collectors.toMap(it -> it, it -> new Membership("Foo")));
    return new OrganizationEntityEventDto(orgId, orgName, memberMap);
  }

  private UserEntityEventDto createUserEventDto(UUID userId, String userName) {
    return new UserEntityEventDto(userId, userName);
  }

  private User createAndPersistUser(UUID userId) {
    return createAndPersistUser(userId, "John Doe");
  }

  private Organization createAndPersistOrganization(UUID orgId) {
    return createAndPersistOrganization(orgId, "ACME Ltd.", List.of());
  }

  private Organization createAndPersistOrganization(UUID orgId, String orgName) {
    return createAndPersistOrganization(orgId, "ACME Ltd.", List.of());
  }

  private Organization createAndPersistOrganization(UUID orgId, List<UUID> members) {
    return createAndPersistOrganization(orgId, "ACME Ltd.", members);
  }

  private Organization createAndPersistOrganization(UUID orgId, String orgName, List<UUID> members) {
    var organization = new Organization(orgId, orgName);
    organization.setMembers(new HashSet<>(members));
    organizationRepository.save(organization);
    return organization;
  }

  private User createAndPersistUser(UUID userId, String userName) {
    var user = new User(userId, userName);
    userRepository.save(user);
    return user;
  }
}
