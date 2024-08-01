package pl.cezarysanecki.parkingdomain.parking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;

@Getter
@AllArgsConstructor
class ParkingSpotSection {

  private ParkingSpotId parkingSpotId;
  private ParkingSpotSectionId sectionId;
  private Occupant occupiedBy;
  private int version;

  static ParkingSpotSection free(ParkingSpotId parkingSpotId) {
    return new ParkingSpotSection(parkingSpotId, ParkingSpotSectionId.newOne(), null, 0);
  }

  boolean occupy(Occupant occupant) {
    if (occupiedBy != null && !occupiedBy.equals(occupant)) return false;
    this.occupiedBy = occupant;
    return true;
  }

  boolean release(Occupant occupant) {
    if (!occupant.equals(occupiedBy)) return false;
    this.occupiedBy = Occupant.none();
    return true;
  }

  boolean canBeOccupiedBy(Occupant occupant) {
    return occupiedBy == null || occupiedBy.equals(occupant);
  }

}
