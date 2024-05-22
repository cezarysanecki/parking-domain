package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;

public interface ReservationRequesterRepository {

    void save(ReservationRequester reservationRequester);

    Option<ReservationRequester> findBy(ReservationRequesterId reservationRequesterId);

    Option<ReservationRequester> findBy(ReservationRequestId reservationRequestId);

    void removeRequestsFromRequesters(List<ReservationRequestId> reservationRequestIds);

}
