package de.innovationhub.prox.projectservice.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import de.innovationhub.prox.projectservice.AbstractRedpandaIT;
import de.innovationhub.prox.projectservice.RedpandaContainer;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.user.User;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class OwnerEntityListenerIT extends AbstractRedpandaIT {

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  EntityManager em;

  static final String USER_TOPIC = "entity.user.user";
  static final String ORGANIZATION_TOPIC = "entity.organization.organization";

  @Test
  void shouldCreateUserOnUserEvent() throws Exception {
    // Given
    var userId = UUID.randomUUID();
    var userName = "John Doe";

    // When
    var ft = kafkaTemplate
      .send(USER_TOPIC, userId.toString(), """
      {
        "id": "%s",
        "name": "%s"
      }
      """.formatted(userId, userName));
    ft.get(5, TimeUnit.SECONDS);

    // Then
    await()
      .atMost(10, TimeUnit.SECONDS)
      .untilAsserted(() ->
        assertThat(em.find(User.class, userId))
          .isNotNull()
          .extracting(User::getName)
          .isEqualTo(userName)
      );
  }

  @Test
  void shouldUpdateUserOnUserEvent() throws Exception {
    // Given
    var userId = UUID.randomUUID();
    var userName = "John Doe";
    var user = new User(userId, userName);
    em.persist(user);

    assertThat(em.find(User.class, userId))
      .isNotNull()
      .extracting(User::getName)
      .isEqualTo(userName);

    // When
    var ft = kafkaTemplate.send(USER_TOPIC, userId.toString(), """
      {
        "id": "%s",
        "name": "%s"
      }
      """.formatted(userId, "Jane Doe"));
    ft.get(5, TimeUnit.SECONDS);

    // Then
    await()
      .atMost(10, TimeUnit.SECONDS)
      .untilAsserted(() ->
        assertThat(em.find(User.class, userId))
          .isNotNull()
          .extracting(User::getName)
          .isEqualTo("Jane Doe")
      );
  }

  @Test
  void shouldDeleteUserOnNullEvent() throws Exception {
    // Given
    var userId = UUID.randomUUID();
    var userName = "John Doe";
    var user = new User(userId, userName);
    em.persist(user);

    assertThat(em.find(User.class, userId))
      .isNotNull();

    // When
    var ft = kafkaTemplate.send(USER_TOPIC, userId.toString(), null);
    ft.get(5, TimeUnit.SECONDS);

    // Then
    await()
      .atMost(10, TimeUnit.SECONDS)
      .untilAsserted(() ->
        assertThat(em.find(User.class, userId))
          .isNull()
      );
  }

  @Test
  void shouldCreateOrganizationOnOrganizationEvent() throws Exception {
    // Given
    var orgId = UUID.randomUUID();
    var orgName = "ACME Ltd.";

    // When
    var ft = kafkaTemplate.send(ORGANIZATION_TOPIC, orgId.toString(), """
      {
        "id": "%s",
        "name": "%s",
        "members": null
      }
      """.formatted(orgId, orgName));
    ft.get(5, TimeUnit.SECONDS);

    // Then
    await()
      .atMost(10, TimeUnit.SECONDS)
      .untilAsserted(() ->
        assertThat(em.find(Organization.class, orgId))
          .isNotNull()
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
    var org = new Organization(orgId, orgName);
    em.persist(org);

    var member1 = UUID.randomUUID();
    var member2 = UUID.randomUUID();

    assertThat(em.find(Organization.class, orgId))
      .isNotNull()
      .extracting(Organization::getName)
      .isEqualTo(orgName);

    // When
    var ft = kafkaTemplate.send(ORGANIZATION_TOPIC, orgId.toString(), """
      {
        "id": "%s",
        "name": "%s",
        "members": {
          "%s": {
            "role": "ADMIN"
          },
          "%s": {
            "role": "ADMIN"
          }
        }
      }
      """.formatted(orgId, "ACME Corporation", member1, member2));
    ft.get(5, TimeUnit.SECONDS);

    // Then
    await()
      .atMost(10, TimeUnit.SECONDS)
      .untilAsserted(() ->
        assertThat(em.find(Organization.class, orgId))
          .isNotNull()
          .satisfies(organization -> {
            assertThat(organization.getName())
              .isEqualTo("ACME Corporation");
            assertThat(organization.getMembers())
              .hasSize(2)
              .containsExactlyInAnyOrder(member1, member2);
          })

      );
  }

  @Test
  void shouldDeleteOrganizationOnNulLEvent() throws Exception {
    // Given
    var orgId = UUID.randomUUID();
    var orgName = "ACME Ltd.";
    var org = new Organization(orgId, orgName);
    em.persist(org);

    assertThat(em.find(Organization.class, orgId))
      .isNotNull();


    // When
    var ft = kafkaTemplate.send(ORGANIZATION_TOPIC, orgId.toString(), null);
    ft.get(5, TimeUnit.SECONDS);

    // Then
    await()
      .atMost(10, TimeUnit.SECONDS)
      .untilAsserted(() ->
        assertThat(em.find(Organization.class, orgId))
          .isNull()
      );
  }
}
