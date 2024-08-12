package pl.cezarysanecki.parkingdomain.management.parkingspot;

import java.util.UUID;

public record ParkingSpotId(
    UUID value
) {

  public static ParkingSpotId newOne() {
    return new ParkingSpotId(UUID.randomUUID());
  }

  public UUID id() {
    return value;
  }

}
