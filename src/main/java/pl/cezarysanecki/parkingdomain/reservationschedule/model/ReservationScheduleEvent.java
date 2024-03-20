package pl.cezarysanecki.parkingdomain.reservationschedule.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

public sealed interface ReservationScheduleEvent extends DomainEvent {

    ParkingSpotId getParkingSpotId();

    @Value
    final class ReservationMade implements ReservationScheduleEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;
        @NonNull ReservationSlot reservationSlot;

    }

    @Value
    final class ReservationFailed implements ReservationScheduleEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;
        @NonNull String reason;

    }

    @Value
    final class ReservationCancelled implements ReservationScheduleEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;

    }

    @Value
    final class ReservationCancellationFailed implements ReservationScheduleEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;
        @NonNull String reason;

    }

    @Value
    final class ReservationBecomeEffective implements ReservationScheduleEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;

    }

}
