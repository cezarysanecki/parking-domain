package pl.cezarysanecki.parkingdomain.management.parkingspot.api;

import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

public record ParkingSpotCapacity(int value) {

  public ParkingSpotCapacity {
    if (value < 0) throw new IllegalArgumentException("value cannot be negative");
  }

  public static ParkingSpotCapacity defaultCapacity() {
    return new ParkingSpotCapacity(4);
  }

  public SpotUnits toSpotUnits() {
    return new SpotUnits(value);
  }

}

