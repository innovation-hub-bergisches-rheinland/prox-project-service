package de.innovationhub.prox.projectservice.owners;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserEntityEventDto(UUID id, String name) { }
