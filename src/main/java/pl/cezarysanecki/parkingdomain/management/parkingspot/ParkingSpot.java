package pl.cezarysanecki.parkingdomain.management.parkingspot;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;

import java.util.List;

public record ParkingSpot(
    ParkingSpotId parkingSpotId,
    List<ParkingSpotSectionId> sections,
    ParkingSpotCategory category) {

}
