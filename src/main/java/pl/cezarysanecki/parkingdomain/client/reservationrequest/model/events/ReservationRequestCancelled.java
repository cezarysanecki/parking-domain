package pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

@Value
public class ReservationRequestCancelled implements ClientReservationRequestsEvent {

    @NonNull ClientId clientId;
    @NonNull ReservationId reservationId;

}
