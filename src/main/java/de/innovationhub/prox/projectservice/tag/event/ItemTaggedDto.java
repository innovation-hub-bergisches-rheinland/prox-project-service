package de.innovationhub.prox.projectservice.tag.event;

import java.util.Set;
import java.util.UUID;

public record ItemTaggedDto(UUID id, Set<String> tags) {
}
