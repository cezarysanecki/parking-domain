package pl.cezarysanecki.parkingdomain.reservation.parkingspot.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ReservationId;

public interface ParkingSpotReservationEvent extends DomainEvent {

    ParkingSpotId getParkingSpotId();

    @Value
    class PartOfParkingSpotReserved implements ParkingSpotReservationEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;
        @NonNull VehicleSize vehicleSize;

    }

    @Value
    class WholeParkingSpotReserved implements ParkingSpotReservationEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;

    }

    @Value
    class ParkingSpotReservationFailed implements ParkingSpotReservationEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;
        @NonNull String reason;

    }

    @Value
    class ParkingSpotReservationCancelled implements ParkingSpotReservationEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;

    }

    @Value
    class ParkingSpotReservationCancellationFailed implements ParkingSpotReservationEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;
        @NonNull String reason;

    }

}
