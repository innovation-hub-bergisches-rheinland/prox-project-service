package de.innovationhub.prox.projectservice.core.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

class InProcessEventPublisherTest {
  ApplicationEventPublisher eventPublisher = Mockito.mock(ApplicationEventPublisher.class);
  InProcessEventPublisher inProcessEventPublisher = new InProcessEventPublisher(eventPublisher);

  @Test
  void shouldSkipPublishingOnNull() {
    inProcessEventPublisher.publish((Event) null);

    verify(eventPublisher, Mockito.times(0)).publishEvent(any());
  }

  @Test
  void shouldSkipPublishingOnEmptyList() {
    inProcessEventPublisher.publish(List.of());

    verify(eventPublisher, Mockito.times(0)).publishEvent(any());
  }

  @Test
  void shouldPublishSingleEventOnApplicationBus() {
    var event = new TestEvent("1");

    inProcessEventPublisher.publish(event);

    verify(eventPublisher).publishEvent(eq(event));
  }

  @Test
  void shouldPublishMultipleEventsOrderedOnApplicationBus() {
    var event1 = new TestEvent("1");
    var event2 = new TestEvent("2");
    List<Event> events = List.of(event1, event2);

    inProcessEventPublisher.publish(events);

    verify(eventPublisher, times(1)).publishEvent(event1);
    verify(eventPublisher, times(1)).publishEvent(event2);
  }

  record TestEvent(String something) implements Event {}
}
