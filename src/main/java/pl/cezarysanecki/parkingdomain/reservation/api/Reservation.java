package pl.cezarysanecki.parkingdomain.reservation.api;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

public record Reservation(
    ReservationId reservationId,
    ReservationOwnerId ownerId,
    ParkingSpotId parkingSpotId,
    TimeSlot timeSlot,
    SpotUnits spotUnits
) {
}
