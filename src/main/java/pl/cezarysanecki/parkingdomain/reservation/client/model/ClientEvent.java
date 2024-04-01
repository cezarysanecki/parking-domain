package pl.cezarysanecki.parkingdomain.reservation.client.model;

import lombok.NonNull;
import lombok.Value;

public interface ClientEvent {

    ClientId getClientId();

    @Value
    class ReservationRequestSubmitted implements ClientEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;

    }

    @Value
    class ReservationRequestSubmissionFailed implements ClientEvent {

        @NonNull ClientId clientId;
        @NonNull String reason;

    }

}
