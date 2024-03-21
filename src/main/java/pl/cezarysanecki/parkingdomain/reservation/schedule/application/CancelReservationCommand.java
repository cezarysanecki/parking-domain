package pl.cezarysanecki.parkingdomain.reservation.schedule.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

@Value
public class CancelReservationCommand {

    @NonNull ReservationId reservationId;

}
