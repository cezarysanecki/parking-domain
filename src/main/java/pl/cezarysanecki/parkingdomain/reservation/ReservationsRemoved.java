package pl.cezarysanecki.parkingdomain.reservation;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

import java.util.List;

public record ReservationsRemoved(
    List<ReservationId> reservations
) implements DomainEvent {
}
