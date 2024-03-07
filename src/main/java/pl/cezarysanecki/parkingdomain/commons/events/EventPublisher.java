package pl.cezarysanecki.parkingdomain.commons.events;

import java.util.Collection;

public interface EventPublisher {

    void publish(DomainEvent event);

    default void publish(Collection<DomainEvent> events) {
        events.forEach(this::publish);
    }

}
