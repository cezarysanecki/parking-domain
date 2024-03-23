package pl.cezarysanecki.parkingdomain.client.requestreservation.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

public interface ClientReservationRequestsEvent extends DomainEvent {

    ClientId getClientId();

    @Value
    final class ChosenParkingSpotReservationRequested implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationType reservationType;
        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class AnyParkingSpotReservationRequested implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationType reservationType;
        @NonNull ParkingSpotType parkingSpotType;
        @NonNull VehicleSizeUnit vehicleSizeUnit;

    }

    @Value
    final class ReservationRequestFailed implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull String reason;

    }

    @Value
    final class ReservationRequestCancelled implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;

    }

    @Value
    final class CancellationOfReservationRequestFailed implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId;
        @NonNull String reason;

    }

}
