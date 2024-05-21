package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

public interface ParkingSpotReservationRequestsEvents {

    ParkingSpotId parkingSpotId();

    ParkingSpotTimeSlotId parkingSpotTimeSlotId();

    record ReservationRequestsCreated(
            @NonNull ParkingSpotId parkingSpotId,
            @NonNull ParkingSpotTimeSlotId parkingSpotTimeSlotId,
            @NonNull ReservationRequest reservationRequest
    ) implements ParkingSpotReservationRequestsEvents {
    }

    record ReservationRequestStored(
            @NonNull ParkingSpotId parkingSpotId,
            @NonNull ParkingSpotTimeSlotId parkingSpotTimeSlotId,
            @NonNull ReservationRequest reservationRequest
    ) implements ParkingSpotReservationRequestsEvents {
    }

    record ReservationRequestCancelled(
            @NonNull ParkingSpotId parkingSpotId,
            @NonNull ParkingSpotTimeSlotId parkingSpotTimeSlotId,
            @NonNull ReservationRequest reservationRequest
    ) implements ParkingSpotReservationRequestsEvents {
    }

    record ReservationRequestConfirmed(
            @NonNull ParkingSpotId parkingSpotId,
            @NonNull ParkingSpotTimeSlotId parkingSpotTimeSlotId,
            @NonNull ValidReservationRequest validReservationRequest
    ) implements ParkingSpotReservationRequestsEvents, DomainEvent {
    }

}
