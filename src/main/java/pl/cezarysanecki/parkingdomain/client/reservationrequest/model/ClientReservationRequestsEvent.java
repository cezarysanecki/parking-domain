package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.application.ReservingPartOfParkingSpotRequestHasOccurred;
import pl.cezarysanecki.parkingdomain.reservation.application.ReservingWholeParkingSpotRequestHasOccurred;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod;

import java.util.UUID;

public interface ClientReservationRequestsEvent extends DomainEvent {

    ClientId getClientId();

    ReservationId getReservationId();

    @Value
    final class ChosenParkingSpotReservationRequested implements ClientReservationRequestsEvent, ReservingWholeParkingSpotRequestHasOccurred {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId = ReservationId.of(UUID.randomUUID());
        @NonNull ReservationPeriod reservationPeriod;
        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class AnyParkingSpotReservationRequested implements ClientReservationRequestsEvent, ReservingPartOfParkingSpotRequestHasOccurred {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId = ReservationId.of(UUID.randomUUID());
        @NonNull ReservationPeriod reservationPeriod;
        @NonNull ParkingSpotType parkingSpotType;
        @NonNull VehicleSizeUnit vehicleSizeUnit;

    }

    @Value
    final class ReservationRequestFailed implements ClientReservationRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull ReservationId reservationId = ReservationId.of(UUID.randomUUID());
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
