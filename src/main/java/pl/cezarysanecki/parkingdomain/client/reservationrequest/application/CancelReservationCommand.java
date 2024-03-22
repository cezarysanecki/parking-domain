package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

class CancelReservationCommand {

    @NonNull ReservationId reservationId;

}
