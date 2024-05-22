package pl.cezarysanecki.parkingdomain.requestingreservation.model;

import io.vavr.collection.List;

public interface ReservationRequestEventPublisher {

    void publish(ReservationRequestEvent event);

    default void publish(List<ReservationRequestEvent> events) {
        events.forEach(this::publish);
    }

}
