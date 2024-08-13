package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;

import java.util.List;

record RequestableParkingSpotTemplate(
    ParkingSpotId parkingSpotId,
    List<ParkingSpotSectionId> sections
) {
}
