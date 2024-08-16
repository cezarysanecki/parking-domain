package pl.cezarysanecki.parkingdomain.reservation;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservedSpace;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

record Reservation(
    ReservationId reservationId,
    ReservationOwnerId ownerId,
    ParkingSpotId parkingSpotId,
    TimeSlot timeSlot,
    SpotUnits spotUnits
) implements ReservedSpace {
}
