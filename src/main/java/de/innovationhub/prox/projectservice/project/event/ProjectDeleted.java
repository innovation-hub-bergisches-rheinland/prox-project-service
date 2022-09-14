package de.innovationhub.prox.projectservice.project.event;

import de.innovationhub.prox.projectservice.core.event.Event;
import java.util.UUID;

public record ProjectDeleted(
  UUID projectId
) implements Event {

}
