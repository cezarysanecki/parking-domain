package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

import java.util.UUID;

public interface ClientReservationRequestsEvent extends DomainEvent {

    ClientId getClientId();

    ClientReservationRequestId getClientReservationRequestId();

    @Value
    final class ChosenParkingSpotReservationRequested implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ClientReservationRequestId clientReservationRequestId = ClientReservationRequestId.of(UUID.randomUUID());
        @NonNull ReservationType reservationType;
        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class AnyParkingSpotReservationRequested implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ClientReservationRequestId clientReservationRequestId = ClientReservationRequestId.of(UUID.randomUUID());
        @NonNull ReservationType reservationType;
        @NonNull ParkingSpotType parkingSpotType;
        @NonNull VehicleSizeUnit vehicleSizeUnit;

    }

    @Value
    final class ReservationRequestFailed implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ClientReservationRequestId clientReservationRequestId = ClientReservationRequestId.of(UUID.randomUUID());
        @NonNull String reason;

    }

    @Value
    final class ReservationRequestCancelled implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ClientReservationRequestId clientReservationRequestId;

    }

    @Value
    final class CancellationOfReservationRequestFailed implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ClientReservationRequestId clientReservationRequestId;
        @NonNull String reason;

    }

}
