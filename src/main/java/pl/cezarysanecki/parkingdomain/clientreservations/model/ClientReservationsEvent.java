package pl.cezarysanecki.parkingdomain.clientreservations.model;

import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

public interface ClientReservationsEvent extends DomainEvent {

    ClientId getClientId();

    @Value
    final class ReservationRequestCreated implements ClientReservationsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationSlot reservationSlot;
        @NonNull Option<ParkingSpotId> parkingSpotId;

        public static ReservationRequestCreated with(ClientId clientId, ReservationSlot reservationSlot) {
            return new ReservationRequestCreated(clientId, reservationSlot, Option.none());
        }

        public static ReservationRequestCreated with(ClientId clientId, ReservationSlot reservationSlot, ParkingSpotId parkingSpotId) {
            return new ReservationRequestCreated(clientId, reservationSlot, Option.of(parkingSpotId));
        }

    }

    @Value
    final class ReservationRequestFailed implements ClientReservationsEvent {

        @NonNull ClientId clientId;
        @NonNull String reason;

    }

}
