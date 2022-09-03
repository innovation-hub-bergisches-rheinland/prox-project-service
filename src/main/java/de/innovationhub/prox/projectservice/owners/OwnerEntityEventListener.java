package de.innovationhub.prox.projectservice.owners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OwnerEntityEventListener {
  private final OrganizationRepository organizationRepository;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  public OwnerEntityEventListener(OrganizationRepository organizationRepository,
    UserRepository userRepository, ObjectMapper objectMapper) {
    this.organizationRepository = organizationRepository;
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = "entity.user.user")
  public void handleUserEntityEvent(ConsumerRecord<String, String> record)
    throws JsonProcessingException {
    var key = UUID.fromString(record.key());
    var value = record.value();

    // Thombstone
    if(value == null) {
      this.userRepository.deleteById(key);
      return;
    }

    var parsedValue = objectMapper.readValue(value, UserEntityEventDto.class);
    if(!key.equals(parsedValue.id())) {
      throw new RuntimeException("Key does not match value");
    }

    var user = this.userRepository.findById(key).orElseGet(() -> new User(key, parsedValue.name()));
    user.setName(parsedValue.name());
    this.userRepository.save(user);
  }

  @KafkaListener(topics = "entity.organization.organization")
  public void handleOrganizationEntityEvent(ConsumerRecord<String, String> record)
    throws JsonProcessingException {
    var key = UUID.fromString(record.key());
    var value = record.value();

    // Thombstone
    if(value == null) {
      this.organizationRepository.deleteById(key);
      return;
    }

    var parsedValue = objectMapper.readValue(value, OrganizationEntityEventDto.class);
    if(!key.equals(parsedValue.id())) {
      throw new RuntimeException("Key does not match value");
    }

    var org = this.organizationRepository.findById(key).orElseGet(() -> new Organization(key, parsedValue.name()));
    org.setName(parsedValue.name());
    this.organizationRepository.save(org);
  }
}
