package pl.cezarysanecki.parkingdomain.requestingreservation;

import io.vavr.collection.List;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;

public interface RequestingReservationIntegrationEvent {

    record ReservationRequestsConfirmed(
            ParkingSpotId parkingSpotId,
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            List<ReservationRequest> reservationRequests
    ) implements RequestingReservationIntegrationEvent {
    }

}
