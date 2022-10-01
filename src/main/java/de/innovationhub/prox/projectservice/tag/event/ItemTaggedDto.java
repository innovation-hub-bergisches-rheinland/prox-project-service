package de.innovationhub.prox.projectservice.tag.event;

import java.util.Set;
import java.util.UUID;

public record ItemTaggedDto(UUID item, Set<Tag> tags) {
  public record Tag(String tag) {}
}
