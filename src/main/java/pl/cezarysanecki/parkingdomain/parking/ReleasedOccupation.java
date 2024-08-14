package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;

import java.util.List;

record ReleasedOccupation(
    OccupationId occupationId,
    OccupantId occupantId,
    ParkingSpotId parkingSpotId,
    List<ParkingSpotSectionId> sections
) {
}
