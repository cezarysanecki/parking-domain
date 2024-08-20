package pl.cezarysanecki.parkingdomain.occupationreleasenotification;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;

import java.time.Instant;

record OccupantToNotify(
    OccupantId occupantId,
    ParkingSpotId parkingSpotId,
    Instant reservationStartDate
) {
}
