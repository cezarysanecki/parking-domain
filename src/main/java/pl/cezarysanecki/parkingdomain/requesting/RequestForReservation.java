package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

record RequestForReservation(
    RequestId requestId,
    RequesterId requesterId,
    ParkingSpotId parkingSpotId,
    TimeSlot timeSlot,
    SpotUnits spotUnits) {
}
