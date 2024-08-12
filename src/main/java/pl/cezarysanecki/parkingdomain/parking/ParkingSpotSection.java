package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;

record ParkingSpotSection(
    ParkingSpotId parkingSpotId,
    ParkingSpotSectionId sectionId,
    OccupationId currentOccupation,
    int version
) {

  static ParkingSpotSection free(ParkingSpotId parkingSpotId) {
    return new ParkingSpotSection(parkingSpotId, ParkingSpotSectionId.newOne(), null, 0);
  }

  boolean isFree() {
    return currentOccupation == null;
  }

}
