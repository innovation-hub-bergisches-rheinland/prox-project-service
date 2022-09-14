package de.innovationhub.prox.projectservice.core.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publishes events using an in-process message bus.
 */
@Component
public class InProcessEventPublisher implements EventPublisher {
  private final ApplicationEventPublisher eventPublisher;

  public InProcessEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void publish(Event event) {
    this.eventPublisher.publishEvent(event);
  }
}
