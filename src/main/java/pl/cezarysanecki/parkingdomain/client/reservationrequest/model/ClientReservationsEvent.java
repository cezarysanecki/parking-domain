package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.application.ReservationRequestHasOccurred;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

import java.util.UUID;

public interface ClientReservationsEvent extends DomainEvent {

    ClientId getClientId();

    @Value
    final class ReservationRequestCreated implements ClientReservationsEvent, ReservationRequestHasOccurred {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId = ReservationId.of(UUID.randomUUID());
        @NonNull Option<ParkingSpotId> parkingSpotId;

        public static ReservationRequestCreated with(ClientId clientId) {
            return new ReservationRequestCreated(clientId, Option.none());
        }

        public static ReservationRequestCreated with(ClientId clientId, ParkingSpotId parkingSpotId) {
            return new ReservationRequestCreated(clientId, Option.of(parkingSpotId));
        }

    }

    @Value
    final class ReservationRequestFailed implements ClientReservationsEvent {

        @NonNull ClientId clientId;
        @NonNull String reason;

    }

    @Value
    final class ReservationRequestApproved implements ClientReservationsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;

    }

    @Value
    final class ReservationRequestCancelled implements ClientReservationsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;

    }

}
