package pl.cezarysanecki.parkingdomain.reservation.client.model;

import lombok.NonNull;
import lombok.Value;

public interface ClientReservationsEvent {

    ClientId getClientId();

    @Value
    class ReservationRequestSubmitted implements ClientReservationsEvent {

        @NonNull ReservationRequest reservationRequest;

        @Override
        public ClientId getClientId() {
            return reservationRequest.getClientId();
        }

    }

    @Value
    class ReservationRequestSubmissionFailed implements ClientReservationsEvent {

        @NonNull ClientId clientId;
        @NonNull String reason;

    }

}
