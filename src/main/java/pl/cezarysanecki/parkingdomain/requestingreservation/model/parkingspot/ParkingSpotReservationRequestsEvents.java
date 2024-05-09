package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

public interface ParkingSpotReservationRequestsEvents extends DomainEvent {

    ParkingSpotId parkingSpotId();

    record ReservationRequestStored(
            @NonNull ParkingSpotId parkingSpotId,
            @NonNull ReservationRequest reservationRequest
    ) implements ParkingSpotReservationRequestsEvents {
    }

    record ReservationRequestConfirmed(
            @NonNull ParkingSpotId parkingSpotId,
            @NonNull ReservationRequest reservationRequest
    ) implements ParkingSpotReservationRequestsEvents {
    }

    record ReservationRequestCancelled(
            @NonNull ParkingSpotId parkingSpotId,
            @NonNull ReservationRequest reservationRequest
    ) implements ParkingSpotReservationRequestsEvents {
    }

}
