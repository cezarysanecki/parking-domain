package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;

record ParkingSpotSection(
    ParkingSpotId parkingSpotId,
    ParkingSpotSectionId sectionId,
    OccupationId currentOccupation,
    int version
) {

  static ParkingSpotSection free(ParkingSpotId parkingSpotId, ParkingSpotSectionId sectionId) {
    return new ParkingSpotSection(parkingSpotId, sectionId, null, 0);
  }

  boolean isFree() {
    return currentOccupation == null;
  }

}
