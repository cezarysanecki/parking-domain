package pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester;

import io.vavr.control.Try;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;

public record ReservationRequester(
    @NonNull ReservationRequesterId requesterId,
    int currentUsage,
    int limit,
    @NonNull Version version
) {

  public ReservationRequester {
    if (currentUsage > limit) {
      throw new IllegalStateException("current usage cannot exceed limit");
    }
    if (limit <= 0) {
      throw new IllegalStateException("value of limit must be positive");
    }
  }

  public Try<ReservationRequestId> append(ReservationRequestId reservationRequestId) {
    if (willBeTooManyRequests(reservationRequestId)) {
      return Try.failure(new IllegalStateException("too many reservation requests"));
    }
    return Try.of(() -> reservationRequestId);
  }

  private boolean willBeTooManyRequests(ReservationRequestId reservationRequestId) {
    return currentUsage + 1 > limit;
  }

}
