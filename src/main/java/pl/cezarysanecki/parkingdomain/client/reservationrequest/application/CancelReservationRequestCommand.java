package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestId;

@Value
public class CancelReservationRequestCommand {

    @NonNull ClientId clientId;
    @NonNull ClientReservationRequestId clientReservationRequestId;

}
