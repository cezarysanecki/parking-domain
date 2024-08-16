package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

record Request(
    RequestId requestId,
    Requester requester,
    ParkingSpotId parkingSpotId,
    TimeSlot timeSlot,
    SpotUnits spotUnits,
    RequestableParkingSpot requestableParkingSpot) {
}
