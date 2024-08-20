package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

record Reservation(
    ReservationId reservationId,
    SpotUnits spotUnits) {
}
