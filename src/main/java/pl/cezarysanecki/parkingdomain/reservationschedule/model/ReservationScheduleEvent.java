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

        @NonNull ReservationId reservationId;
        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationSlot reservationSlot;
        @NonNull ClientId clientId;

    }

    @Value
    final class ReservationFailed implements ReservationScheduleEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull String reason;

    }

    @Value
    final class ReservationCancelled implements ReservationScheduleEvent {

        @NonNull ReservationId reservationId;
        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ClientId clientId;

    }

    @Value
    final class ReservationCancellationFailed implements ReservationScheduleEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;
        @NonNull String reason;

    }

}
