package pl.cezarysanecki.parkingdomain.reservation.model;

import lombok.NonNull;
import lombok.Value;

public interface Reservation {

    ReservationId getReservationId();

    @Value
    class Individual implements Reservation {

        @NonNull ReservationId reservationId;

    }

    @Value
    class Collective implements Reservation {

        @NonNull ReservationId reservationId;

    }

}
