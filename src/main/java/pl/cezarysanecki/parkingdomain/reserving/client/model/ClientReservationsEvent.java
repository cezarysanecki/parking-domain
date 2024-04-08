package pl.cezarysanecki.parkingdomain.reserving.client.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;

public interface ClientReservationsEvent extends DomainEvent {

    ClientId getClientId();

    @Value
    class ReservationForPartOfParkingSpotSubmitted implements ClientReservationsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;
        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleSize vehicleSize;

    }

    @Value
    class ReservationForWholeParkingSpotSubmitted implements ClientReservationsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;
        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    class ReservationSubmissionFailed implements ClientReservationsEvent {

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
