package pl.cezarysanecki.parkingdomain.requesting;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
class ReservationRequesterEntity {

  UUID requesterId;
  int limit;
  int version;

  ReservationRequester toDomain(List<ReservationRequestEntity> reservationRequests) {
    return new ReservationRequester(
        RequesterId.of(requesterId),
        reservationRequests.size(),
        limit,
        new Version(version));
  }

}
