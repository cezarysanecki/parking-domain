package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;

public interface ParkingSpotReservationRequestEvent extends DomainEvent {

    ParkingSpotId getParkingSpotId();

    @Value
    class PartRequestOfParkingSpotReserved implements ParkingSpotReservationRequestEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;
        @NonNull VehicleSize vehicleSize;

    }

    @Value
    class WholeRequestParkingSpotReserved implements ParkingSpotReservationRequestEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;

    }

    @Value
    class ParkingSpotReservationRequestFailed implements ParkingSpotReservationRequestEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;
        @NonNull String reason;

    }

    @Value
    class ParkingSpotReservationRequestCancelled implements ParkingSpotReservationRequestEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;

    }

    @Value
    class ParkingSpotReservationRequestCancellationFailed implements ParkingSpotReservationRequestEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;
        @NonNull String reason;

    }

}
