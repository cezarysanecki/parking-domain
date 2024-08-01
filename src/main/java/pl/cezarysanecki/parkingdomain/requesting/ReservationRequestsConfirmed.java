package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

public record ReservationRequestsConfirmed(
    ParkingSpotId parkingSpotId,
    ReservationRequest reservationRequest
) implements DomainEvent {
}
