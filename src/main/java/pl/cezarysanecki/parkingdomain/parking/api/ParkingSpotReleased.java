package pl.cezarysanecki.parkingdomain.parking.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;

import java.util.List;

public record ParkingSpotReleased(
    OccupationId occupationId,
    OccupantId occupantId,
    ParkingSpotId parkingSpotId,
    List<ParkingSpotSectionId> sections
) implements DomainEvent {
}
