package pl.cezarysanecki.parkingdomain.parking.api;

import java.util.UUID;

public record OccupantId(
    UUID value
) {

  public static OccupantId none() {
    return new OccupantId(null);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
