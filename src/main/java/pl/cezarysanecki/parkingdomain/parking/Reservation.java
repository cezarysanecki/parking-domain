package pl.cezarysanecki.parkingdomain.parking;

import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits;

record Reservation(
        @NonNull ReservationId reservationId,
        @NonNull SpotUnits spotUnits
) {
}
