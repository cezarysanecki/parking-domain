package pl.cezarysanecki.parkingdomain.requesting;

import io.vavr.control.Try;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

public record ReservationRequestsTimeSlot(
    @NonNull ReservationRequestsTimeSlotId timeSlotId,
    int currentUsage,
    int capacity,
    @NonNull Version version) {

  public ReservationRequestsTimeSlot {
    if (currentUsage > capacity) {
      throw new IllegalStateException("current usage cannot exceed capacity");
    }
    if (capacity <= 0) {
      throw new IllegalStateException("capacity must be positive");
    }
  }

  public Try<SpotUnits> append(SpotUnits spotUnits) {
    if (exceedsAllowedSpace(spotUnits)) {
      return Try.failure(new IllegalStateException("not enough space"));
    }
    return Try.of(() -> spotUnits);
  }

  private boolean exceedsAllowedSpace(SpotUnits spotUnits) {
    return currentUsage + spotUnits.getValue() > capacity;
  }

}
