package pl.cezarysanecki.parkingdomain.parking.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

import java.util.List;

public record ParkingSpotReleased(
    OccupationId occupationId,
    Occupant occupant,
    ParkingSpotId parkingSpotId,
    List<ParkingSpotSectionId> sections
) implements DomainEvent {
}
