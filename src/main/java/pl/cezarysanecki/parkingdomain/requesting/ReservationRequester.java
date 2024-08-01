package pl.cezarysanecki.parkingdomain.requesting;

import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.requesting.api.ReservationRequestId;

import java.util.List;

public record ReservationRequester(
    @NonNull RequesterId requesterId,
    List<ReservationRequestId> requests,
    int limit,
    @NonNull Version version
) {

  public ReservationRequester {
    if (requests.size() > limit) {
      throw new IllegalStateException("current usage cannot exceed limit");
    }
    if (limit == 0) {
      throw new IllegalStateException("value of limit must be positive");
    }
  }

  boolean append(ReservationRequestId reservationRequestId) {
    if (willBeTooManyRequests(reservationRequestId)) {
      requests.add(reservationRequestId);
      return true;
    }
    return false;
  }

  private boolean willBeTooManyRequests(ReservationRequestId reservationRequestId) {
    return requests.size() + 1 > limit;
  }

  boolean cancel(ReservationRequestId reservationRequestId) {
    return false;
  }

}
