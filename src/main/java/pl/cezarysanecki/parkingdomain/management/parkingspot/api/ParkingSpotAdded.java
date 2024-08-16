package pl.cezarysanecki.parkingdomain.management.parkingspot.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

public record ParkingSpotAdded(
    ParkingSpotId parkingSpotId,
    ParkingSpotCapacity capacity
) implements DomainEvent {
}
