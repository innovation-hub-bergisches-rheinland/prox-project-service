package de.innovationhub.prox.projectservice.core.event;

import java.util.Collection;

public interface EventPublisher {
  void publish(Event event);

  /**
   * Publishes events in the received order
   * @param events
   */
  void publish(Collection<Event> events);
}
