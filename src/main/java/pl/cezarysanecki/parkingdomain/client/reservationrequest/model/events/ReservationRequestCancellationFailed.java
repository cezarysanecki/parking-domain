package pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

@Value
public class ReservationRequestCancellationFailed implements ClientReservationRequestsEvent {

    @NonNull ClientId clientId;
    @NonNull ReservationId reservationId;
    @NonNull String reason;

}
