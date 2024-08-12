package pl.cezarysanecki.parkingdomain.shared.occupation;

public record ParkingSpotCapacity(int value) {

  public ParkingSpotCapacity {
    if (value < 0) throw new IllegalArgumentException("value cannot be negative");
  }

  public static ParkingSpotCapacity defaultCapacity() {
    return new ParkingSpotCapacity(4);
  }

}

