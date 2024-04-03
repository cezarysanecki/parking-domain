package pl.cezarysanecki.parkingdomain.reservation.client.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

public interface ClientReservationsEvent extends DomainEvent {

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

    @Value
    class ReservationRequestCancelled implements ClientReservationsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;

    }

    @Value
    class ReservationRequestCancellationFailed implements ClientReservationsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;
        @NonNull String reason;

    }

}
