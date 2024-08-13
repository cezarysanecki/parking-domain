package pl.cezarysanecki.parkingdomain.management.parkingspot.api;

import java.util.UUID;

public record ParkingSpotId(
    UUID value
) {

  public static ParkingSpotId newOne() {
    return new ParkingSpotId(UUID.randomUUID());
  }

}
