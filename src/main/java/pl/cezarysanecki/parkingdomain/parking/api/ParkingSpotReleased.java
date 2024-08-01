package pl.cezarysanecki.parkingdomain.parking.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

public record ParkingSpotReleased(
    ParkingSpotId parkingSpotId
) implements DomainEvent {
}
