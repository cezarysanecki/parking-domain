package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

record ReleasedOccupation(
    OccupationId occupationId,
    OccupantId occupantId,
    ParkingSpotId parkingSpotId,
    SpotUnits spotUnits
) {
}
