package pl.cezarysanecki.parkingdomain.reservation.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

public record ReservationUsed(
    ReservationId reservationId
) implements DomainEvent {
}
