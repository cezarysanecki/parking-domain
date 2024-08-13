package pl.cezarysanecki.parkingdomain.parking.api;

import java.util.UUID;

public record ReservationOwnerId(
    UUID value
) {

  public static ReservationOwnerId none() {
    return new ReservationOwnerId(null);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
