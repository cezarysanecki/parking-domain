package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Value
public class ValidReservationRequest {

    @NonNull
    ReservationRequesterId reservationRequesterId;
    @NonNull
    ReservationRequestId reservationRequestId;
    @NonNull
    SpotUnits spotUnits;

    public static ValidReservationRequest from(ReservationRequest reservationRequest) {
        return new ValidReservationRequest(
                reservationRequest.getReservationRequesterId(),
                reservationRequest.getReservationRequestId(),
                reservationRequest.getSpotUnits());
    }

}
