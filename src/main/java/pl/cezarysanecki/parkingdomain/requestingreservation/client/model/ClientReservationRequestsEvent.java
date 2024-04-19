package pl.cezarysanecki.parkingdomain.requestingreservation.client.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;

public interface ClientReservationRequestsEvent extends DomainEvent {

    ClientId getClientId();

    @Value
    class ReservationForPartOfParkingSpotRequested implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;
        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleSize vehicleSize;

    }

    @Value
    class ReservationForWholeParkingSpotRequested implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;
        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    class RequestingReservationFailed implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull String reason;

    }

    @Value
    class ReservationRequestCancelled implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;

    }

    @Value
    class ReservationRequestCancellationFailed implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;
        @NonNull String reason;

    }

}
