package pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import java.util.UUID;

@Value
public class ReservationRequestFailed implements ClientReservationRequestsEvent {

    @NonNull ClientId clientId;
    @NonNull ReservationId reservationId = ReservationId.of(UUID.randomUUID());
    @NonNull String reason;

}
