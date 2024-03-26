package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import java.time.LocalDateTime;

@Value
public class CancelReservationRequestCommand {

    @NonNull ReservationId reservationId;
    @NonNull LocalDateTime when;
}
