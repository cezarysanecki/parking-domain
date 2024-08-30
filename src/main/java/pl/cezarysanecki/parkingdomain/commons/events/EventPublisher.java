package pl.cezarysanecki.parkingdomain.commons.events;

import java.util.List;

public interface EventPublisher {

  void publish(DomainEvent event);

  default void publish(List<DomainEvent> events) {
    events.forEach(this::publish);
  }

}
