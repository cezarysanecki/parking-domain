package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.collection.HashSet;
import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
class ReservationRequesterEntity {

  UUID requesterId;
  Set<UUID> currentRequests;
  int limit;
  int version;

  ReservationRequesterEntity handle(ReservationRequesterEvent event) {
    return switch (event) {
      case ReservationRequesterEvent.ReservationRequestAppended appended -> {
        currentRequests.add(appended.reservationRequestId().getValue());
        yield this;
      }
      case ReservationRequesterEvent.ReservationRequestRemoved removed -> {
        currentRequests.remove(removed.reservationRequestId().getValue());
        yield this;
      }
      default -> this;
    };
  }

  ReservationRequester toDomain() {
    return new ReservationRequester(
        ReservationRequesterId.of(requesterId),
        HashSet.ofAll(currentRequests.stream().map(ReservationRequestId::of)),
        limit,
        new Version(version));
  }

}
