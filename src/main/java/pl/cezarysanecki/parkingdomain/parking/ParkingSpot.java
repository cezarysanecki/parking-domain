package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

record ParkingSpot(
    ParkingSpotId parkingSpotId,
    int occupiedSpace,
    int reservedSpace,
    ParkingSpotCapacity capacity,
    Version version
) {

  static ParkingSpot create(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity) {
    return new ParkingSpot(parkingSpotId, 0, 0, capacity, Version.zero());
  }

  boolean occupyBy(SpotUnits spotUnits) {
    return (capacity.value() - occupiedSpace - reservedSpace) >= spotUnits.value();
  }

}
