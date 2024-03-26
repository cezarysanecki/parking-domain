package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

@Value
public class CancelReservationRequestCommand {

    @NonNull ReservationId reservationId;

}
