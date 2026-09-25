package pl.cezarysanecki.parkingdomain.shared;

public enum VehicleType {

  CAR(4),
  MOTORCYCLE(2),
  SCOOTER(1);

  private final SpotUnits spotUnits;

  VehicleType(int spotUnits) {
    this.spotUnits = new SpotUnits(spotUnits);
  }

  public SpotUnits spotUnits() {
    return spotUnits;
  }

}
