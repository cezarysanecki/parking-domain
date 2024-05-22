package pl.cezarysanecki.parkingdomain.requestingreservation.model;

import io.vavr.collection.List;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;

public interface ReservationRequestEvent {

    ParkingSpotId parkingSpotId();

    ReservationRequestsTimeSlotId reservationRequestsTimeSlotId();

    record ReservationRequestStored(
            ParkingSpotId parkingSpotId,
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            ReservationRequest reservationRequest
    ) implements ReservationRequestEvent {
    }

    record ReservationRequestCancelled(
            ParkingSpotId parkingSpotId,
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            ReservationRequest reservationRequest
    ) implements ReservationRequestEvent {
    }

    record ReservationRequestsConfirmed(
            ParkingSpotId parkingSpotId,
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            List<ReservationRequest> reservationRequests
    ) implements ReservationRequestEvent, DomainEvent {
    }

}
