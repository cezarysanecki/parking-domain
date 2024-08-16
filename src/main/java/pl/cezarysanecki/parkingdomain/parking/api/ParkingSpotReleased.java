package pl.cezarysanecki.parkingdomain.parking.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

public record ParkingSpotReleased(
    OccupationId occupationId,
    OccupantId occupantId,
    ParkingSpotId parkingSpotId,
    SpotUnits spotUnits
) implements DomainEvent {
}
