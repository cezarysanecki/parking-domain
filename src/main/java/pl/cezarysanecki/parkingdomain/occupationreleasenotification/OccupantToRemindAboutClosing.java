package pl.cezarysanecki.parkingdomain.occupationreleasenotification;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;

record OccupantToRemindAboutClosing(
    OccupantId occupantId,
    ParkingSpotId parkingSpotId
) {
}
