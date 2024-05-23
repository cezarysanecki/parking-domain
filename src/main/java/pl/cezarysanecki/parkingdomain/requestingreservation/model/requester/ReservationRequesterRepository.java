package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;

public interface ReservationRequesterRepository {

    void publish(ReservationRequesterEvent event);

    Option<ReservationRequester> findBy(ReservationRequesterId reservationRequesterId);

    Option<ReservationRequester> findBy(ReservationRequestId reservationRequestId);

}
