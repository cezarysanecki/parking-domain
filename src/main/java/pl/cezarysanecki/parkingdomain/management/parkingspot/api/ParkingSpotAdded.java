package pl.cezarysanecki.parkingdomain.management.parkingspot.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

import java.util.List;

public record ParkingSpotAdded(
    ParkingSpotId parkingSpotId,
    List<ParkingSpotSectionId> sections
) implements DomainEvent {
}
