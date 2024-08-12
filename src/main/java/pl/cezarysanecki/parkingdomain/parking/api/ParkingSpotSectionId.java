package pl.cezarysanecki.parkingdomain.parking.api;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ParkingSpotSectionId {

  UUID value;

  public static ParkingSpotSectionId newOne() {
    return new ParkingSpotSectionId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
