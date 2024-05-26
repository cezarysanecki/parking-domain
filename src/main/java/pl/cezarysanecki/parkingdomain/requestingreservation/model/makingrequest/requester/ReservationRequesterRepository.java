package pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester;

import io.vavr.control.Option;

public interface ReservationRequesterRepository {

  void saveNew(ReservationRequesterId requesterId, int limit);

  Option<ReservationRequester> findBy(ReservationRequesterId reservationRequesterId);

}
