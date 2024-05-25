package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import io.vavr.control.Try;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterEvent.ReservationRequestAppended;

public record ReservationRequester(
    @NonNull ReservationRequesterId requesterId,
    int currentUsage,
    int limit,
    @NonNull Version version
) {

  public static ReservationRequester newOne(ReservationRequesterId requesterId, int limit) {
    return new ReservationRequester(requesterId, 0, limit, Version.zero());
  }

  public Try<ReservationRequestAppended> append(ReservationRequestId reservationRequestId) {
    if (willBeTooManyRequests(reservationRequestId)) {
      return Try.failure(new IllegalStateException("too many reservation requests"));
    }
    return Try.of(() -> new ReservationRequestAppended(requesterId, reservationRequestId, version));
  }

  private boolean willBeTooManyRequests(ReservationRequestId reservationRequestId) {
    return currentUsage + 1 > limit;
  }

}
