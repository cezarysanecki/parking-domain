package pl.cezarysanecki.parkingdomain.parking.api;

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
