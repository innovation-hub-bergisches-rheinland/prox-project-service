package de.innovationhub.prox.projectservice.module.dto;


import de.innovationhub.prox.projectservice.module.Specialization;
import java.util.List;
import java.util.stream.StreamSupport;

public record ReadSpecializationCollectionDto(List<ReadSpecializationDto> specializations) {
  public static ReadSpecializationCollectionDto fromSpecializations(
      Iterable<Specialization> specializations) {
    return new ReadSpecializationCollectionDto(
        StreamSupport.stream(specializations.spliterator(), false)
            .map(ReadSpecializationDto::fromSpecialization)
            .toList());
  }
}
