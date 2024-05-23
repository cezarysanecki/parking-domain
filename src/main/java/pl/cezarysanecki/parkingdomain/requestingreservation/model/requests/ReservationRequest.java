package pl.cezarysanecki.parkingdomain.requestingreservation.model.requests;

import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

public record ReservationRequest(
        ReservationRequestId reservationRequestId,
        ReservationRequesterId reservationRequesterId,
        ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
        SpotUnits spotUnits) {

    public static ReservationRequest newOne(
            ReservationRequesterId requesterId,
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            SpotUnits spotUnits
    ) {
        return new ReservationRequest(
                ReservationRequestId.newOne(),
                requesterId,
                reservationRequestsTimeSlotId,
                spotUnits);
    }

}
