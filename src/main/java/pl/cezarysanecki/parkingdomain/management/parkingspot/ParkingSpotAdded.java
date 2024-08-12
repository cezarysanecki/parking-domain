package pl.cezarysanecki.parkingdomain.management.parkingspot;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;

import java.util.List;

public record ParkingSpotAdded(
    ParkingSpotId parkingSpotId,
    List<ParkingSpotSectionId> sections
) implements DomainEvent {
}
