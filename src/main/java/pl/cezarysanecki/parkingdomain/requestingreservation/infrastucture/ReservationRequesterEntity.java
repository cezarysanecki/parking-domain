package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
class ReservationRequesterEntity {

  UUID requesterId;
  int limit;
  int version;

  ReservationRequester toDomain(List<ReservationRequestEntity> reservationRequests) {
    return new ReservationRequester(
        ReservationRequesterId.of(requesterId),
        reservationRequests.size(),
        limit,
        new Version(version));
  }

}
