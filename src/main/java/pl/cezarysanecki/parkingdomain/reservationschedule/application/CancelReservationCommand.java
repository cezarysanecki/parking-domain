package pl.cezarysanecki.parkingdomain.reservationschedule.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

@Value
public class CancelReservationCommand {

    @NonNull ReservationId reservationId;

}
