package de.innovationhub.prox.projectservice.owners;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.projectservice.KafkaListenerBaseTest;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class OwnerEntityEventListenerTest extends KafkaListenerBaseTest {
  UserRepository userRepository = Mockito.mock(UserRepository.class);
  OrganizationRepository organizationRepository = Mockito.mock(OrganizationRepository.class);
  ObjectMapper objectMapper = new ObjectMapper();
  OwnerEntityEventListener ownerListener = new OwnerEntityEventListener(
    organizationRepository,
    userRepository,
    objectMapper
  );

  @Test
  void shouldDeleteOrganizationOnNull() throws JsonProcessingException {
    var id = UUID.randomUUID();
    ConsumerRecord<String, String> record = createDefaultConsumerRecord(id.toString(), null);

    ownerListener.handleOrganizationEntityEvent(record);

    verify(organizationRepository).deleteById(eq(id));
  }

  @Test
  void shouldCreateOrganization() throws JsonProcessingException {
    var id = UUID.randomUUID();
    var name = "Tes Organization";
    var event = createOrganizationEvent(id, name);
    ConsumerRecord<String, String> record = createDefaultConsumerRecord(id.toString(), event);
    when(organizationRepository.findById(eq(id))).thenReturn(Optional.empty());

    ownerListener.handleOrganizationEntityEvent(record);

    var captor = ArgumentCaptor.forClass(Organization.class);
    verify(organizationRepository).save(captor.capture());
    assertThat(captor.getValue())
      .extracting(Organization::getId, Organization::getName)
      .containsExactly(id, name);
  }

  @Test
  void shouldAddMembersToOrganization() throws JsonProcessingException {
    var id = UUID.randomUUID();
    var name = "Tes Organization";
    var userId = UUID.randomUUID();

    var event = createOrganizationEventWithMember(id, name, userId);

    ConsumerRecord<String, String> record = createDefaultConsumerRecord(id.toString(), event);
    when(organizationRepository.findById(eq(id))).thenReturn(Optional.empty());

    ownerListener.handleOrganizationEntityEvent(record);

    var captor = ArgumentCaptor.forClass(Organization.class);
    verify(organizationRepository).save(captor.capture());
    assertThat(captor.getValue())
      .satisfies(org -> assertThat(org.getMembers()).containsExactly(userId));
  }

  @Test
  void shouldUpdateOrganization() throws JsonProcessingException {
    var id = UUID.randomUUID();
    var name = "Test Organization";
    var org = new Organization(id, name);
    var event = createOrganizationEvent(id, "Updated Name");
    ConsumerRecord<String, String> record = createDefaultConsumerRecord(id.toString(), event);
    when(organizationRepository.findById(eq(id))).thenReturn(Optional.of(org));

    ownerListener.handleOrganizationEntityEvent(record);

    var captor = ArgumentCaptor.forClass(Organization.class);
    verify(organizationRepository).save(captor.capture());
    assertThat(captor.getValue())
      .extracting(Organization::getId, Organization::getName)
      .containsExactly(id, "Updated Name");
  }

  @Test
  void shouldDeleteUserOnNull() throws JsonProcessingException {
    var id = UUID.randomUUID();
    ConsumerRecord<String, String> record = createDefaultConsumerRecord(id.toString(), null);

    ownerListener.handleUserEntityEvent(record);

    verify(userRepository).deleteById(eq(id));
  }

  @Test
  void shouldCreateUser() throws JsonProcessingException {
    var id = UUID.randomUUID();
    var name = "Test User";
    var event = createUserEvent(id, name);
    ConsumerRecord<String, String> record = createDefaultConsumerRecord(id.toString(), event);
    when(userRepository.findById(eq(id))).thenReturn(Optional.empty());

    ownerListener.handleUserEntityEvent(record);

    var captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertThat(captor.getValue())
      .extracting(User::getId, User::getName)
      .containsExactly(id, name);
  }

  @Test
  void shouldUpdateUser() throws JsonProcessingException {
    var id = UUID.randomUUID();
    var name = "Test User";
    var user = new User(id, name);

    var event = createOrganizationEvent(id, "Updated Username");
    ConsumerRecord<String, String> record = createDefaultConsumerRecord(id.toString(), event);
    when(userRepository.findById(eq(id))).thenReturn(Optional.of(user));

    ownerListener.handleUserEntityEvent(record);

    var captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertThat(captor.getValue())
      .extracting(User::getId, User::getName)
      .containsExactly(id, "Updated Username");
  }

  private String createUserEvent(UUID id, String name) {
    return """
      {
        "id": "%s",
        "name": "%s"
      }
      """.formatted(id, name);
  }

  private String createOrganizationEvent(UUID id, String name) {
    return """
      {
        "id": "%s",
        "name": "%s",
        "members": null
      }
      """.formatted(id, name);
  }

  private String createOrganizationEventWithMember(UUID id, String name, UUID memberId) {
    return """
      {
        "id": "%s",
        "name": "%s",
        "members": {
          "%s": {
            "role": null
          }
        }
      }
      """.formatted(id, name, memberId);
  }
}
