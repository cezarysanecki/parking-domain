package pl.cezarysanecki.parkingdomain.management.parkingspot.api;

import java.util.UUID;

public record ParkingSpotSectionId(UUID value) {

  public static ParkingSpotSectionId newOne() {
    return new ParkingSpotSectionId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
