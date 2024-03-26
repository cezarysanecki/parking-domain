package pl.cezarysanecki.parkingdomain.reservation.application;

import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

public interface CancellingReservationHasOccurred {

    ReservationId getReservationId();

}
