package pl.cezarysanecki.parkingdomain.parking.api;

import java.util.UUID;

public record OccupationId(
    UUID value
) {

  public static OccupationId newOne() {
    return new OccupationId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
