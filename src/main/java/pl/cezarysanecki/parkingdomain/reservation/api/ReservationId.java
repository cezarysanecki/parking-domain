package pl.cezarysanecki.parkingdomain.reservation.api;

import java.util.UUID;

public record ReservationId(
    UUID value
) {

  public static ReservationId none() {
    return new ReservationId(null);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
