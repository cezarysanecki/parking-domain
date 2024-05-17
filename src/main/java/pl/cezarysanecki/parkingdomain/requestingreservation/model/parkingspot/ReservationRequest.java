package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Value
public class ReservationRequest {

    @NonNull
    ReservationRequesterId reservationRequesterId;
    @NonNull
    ReservationRequestId reservationRequestId;
    @NonNull
    SpotUnits spotUnits;

    public static ReservationRequest newOne(ReservationRequesterId reservationRequesterId, SpotUnits spotUnits) {
        return new ReservationRequest(reservationRequesterId, ReservationRequestId.newOne(), spotUnits);
    }

}
