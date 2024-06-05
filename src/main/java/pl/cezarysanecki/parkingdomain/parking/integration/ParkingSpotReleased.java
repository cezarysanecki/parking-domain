package pl.cezarysanecki.parkingdomain.parking.integration;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

public record ParkingSpotReleased(
    ParkingSpotId parkingSpotId
) implements DomainEvent {
}
