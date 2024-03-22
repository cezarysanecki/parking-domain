package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

@Value
public class CancelReservationCommand {

    @NonNull ReservationId reservationId;

}
