package pl.cezarysanecki.parkingdomain.management.parkingspot;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

public record ParkingSpotAdded(
        ParkingSpotId parkingSpotId,
        ParkingSpotCapacity capacity,
        ParkingSpotCategory category
) implements DomainEvent {

    ParkingSpotAdded(ParkingSpot parkingSpot) {
        this(parkingSpot.getParkingSpotId(), parkingSpot.getCapacity(), parkingSpot.getCategory());
    }

}
