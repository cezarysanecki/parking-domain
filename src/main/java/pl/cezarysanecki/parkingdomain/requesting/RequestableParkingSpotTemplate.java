package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;

import java.util.List;

record RequestableParkingSpotTemplate(
    ParkingSpotId parkingSpotId,
    List<ParkingSpotSectionId> sections
) {
}
