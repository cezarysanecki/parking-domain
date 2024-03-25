package pl.cezarysanecki.parkingdomain.reservation.schedule.model;

import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

public sealed interface ParkingSpotReservationsEvent extends DomainEvent {

    ReservationId getReservationId();

    @Value
    final class ReservationForWholeParkingSpotMade implements ParkingSpotReservationsEvent {

        @NonNull ReservationId reservationId;
        @NonNull ReservationPeriod reservationPeriod;
        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class ReservationForPartOfParkingSpotMade implements ParkingSpotReservationsEvent {

        @NonNull ReservationId reservationId;
        @NonNull ReservationPeriod reservationPeriod;
        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleSizeUnit vehicleSizeUnit;

    }

    @Value
    final class ReservationFailed implements ParkingSpotReservationsEvent {

        @NonNull ReservationId reservationId;
        @NonNull ReservationPeriod reservationPeriod;
        @NonNull Option<ParkingSpotId> parkingSpotId;
        @NonNull String reason;

    }

    @Value
    final class ReservationCancelled implements ParkingSpotReservationsEvent {

        @NonNull ReservationId reservationId;

    }

}
