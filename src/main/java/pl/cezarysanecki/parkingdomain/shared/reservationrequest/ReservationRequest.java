package pl.cezarysanecki.parkingdomain.shared.reservationrequest;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Value
public class ReservationRequest {

    @NonNull
    ReservationRequestId reservationRequestId;
    @NonNull
    ReservationRequesterId requesterId;
    @NonNull
    ReservationRequestsTimeSlotId reservationRequestsTimeSlotId;
    @NonNull
    SpotUnits spotUnits;

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
