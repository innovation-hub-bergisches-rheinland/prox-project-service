package de.innovationhub.prox.projectservice.module.dto;


import de.innovationhub.prox.projectservice.module.Specialization;
import java.util.UUID;

public record ReadSpecializationDto(UUID id, String name, String key) {
  public static ReadSpecializationDto fromSpecialization(Specialization specialization) {
    return new ReadSpecializationDto(
        specialization.getId(), specialization.getName(), specialization.getKey());
  }
}
