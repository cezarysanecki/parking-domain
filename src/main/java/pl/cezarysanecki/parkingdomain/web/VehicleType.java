package pl.cezarysanecki.parkingdomain.web;

import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

/**
 * Vehicle type is a notion of the HTTP API only. The domain knows just how many spot units
 * of a parking spot are taken, so the web layer translates vehicle type into {@link SpotUnits}.
 */
enum VehicleType {

  CAR(4),
  MOTORCYCLE(2),
  SCOOTER(1);

  private final SpotUnits spotUnits;

  VehicleType(int spotUnits) {
    this.spotUnits = new SpotUnits(spotUnits);
  }

  SpotUnits spotUnits() {
    return spotUnits;
  }

}
