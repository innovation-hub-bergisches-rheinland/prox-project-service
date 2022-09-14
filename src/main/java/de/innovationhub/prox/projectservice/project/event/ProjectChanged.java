package de.innovationhub.prox.projectservice.project.event;

import de.innovationhub.prox.projectservice.core.event.Event;
import de.innovationhub.prox.projectservice.project.Project;

/**
 * This event is being fired when the Project entity changes in any way
 * so we can eventually publish the entity state. Not the smartest way of eventing, but it
 * kinda works
 * @param project the CHANGED project
 */
public record ProjectChanged(
  Project project
) implements Event {

}
