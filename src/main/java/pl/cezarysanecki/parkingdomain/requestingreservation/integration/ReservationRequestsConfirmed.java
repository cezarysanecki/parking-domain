package pl.cezarysanecki.parkingdomain.requestingreservation.integration;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;

public record ReservationRequestsConfirmed(
    ParkingSpotId parkingSpotId,
    ReservationRequest reservationRequest
) implements DomainEvent {
}
