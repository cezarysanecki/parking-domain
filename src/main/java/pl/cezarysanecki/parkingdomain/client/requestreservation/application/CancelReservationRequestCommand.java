package pl.cezarysanecki.parkingdomain.client.requestreservation.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

@Value
public class CancelReservationRequestCommand {

    @NonNull ClientId clientId;
    @NonNull ReservationId reservationId;

}
