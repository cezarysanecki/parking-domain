package pl.cezarysanecki.parkingdomain.reservation.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

public sealed interface ReservationEvent extends DomainEvent {

    ParkingSpotId getParkingSpotId();

    @Value
    final class ReservationMade implements ReservationEvent {

        @NonNull ReservationId reservationId;
        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationSlot reservationSlot;
        @NonNull ClientId clientId;

    }

    @Value
    final class ReservationFailed implements ReservationEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull String reason;

    }

    @Value
    final class ReservationCancelled implements ReservationEvent {

        @NonNull ReservationId reservationId;
        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class ReservationCancellationFailed implements ReservationEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull String reason;

    }

}
