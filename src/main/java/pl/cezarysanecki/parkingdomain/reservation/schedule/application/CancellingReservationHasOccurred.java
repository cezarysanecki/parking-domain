package pl.cezarysanecki.parkingdomain.reservation.schedule.application;

import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

public interface CancellingReservationHasOccurred {

    ReservationId getReservationId();

}
