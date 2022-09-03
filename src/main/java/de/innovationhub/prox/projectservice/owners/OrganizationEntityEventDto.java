package de.innovationhub.prox.projectservice.owners;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micrometer.core.lang.Nullable;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationEntityEventDto(UUID id, String name,
                                         @Nullable Map<UUID, Membership> members) {

  public record Membership(String role) {

  }
}
