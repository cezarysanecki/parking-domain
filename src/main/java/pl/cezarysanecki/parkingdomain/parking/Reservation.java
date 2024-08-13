package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

record Reservation(
    ReservationId reservationId,
    ReservationOwnerId ownerId,
    ParkingSpotId parkingSpotId,
    TimeSlot timeSlot
) {
}
