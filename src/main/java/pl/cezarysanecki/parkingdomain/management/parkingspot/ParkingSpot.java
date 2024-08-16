package pl.cezarysanecki.parkingdomain.management.parkingspot;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

public record ParkingSpot(
    ParkingSpotId parkingSpotId,
    ParkingSpotCapacity capacity,
    ParkingSpotCategory category) {

}
