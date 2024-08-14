package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.List;

record ParkingSpotSectionsGrouped(
    List<ParkingSpotSection> sections,
    int reservedSpace
) {

  ParkingSpotSectionsGrouped {
    if (sections.isEmpty()) {
      throw new IllegalStateException("grouped sections cannot be empty");
    }
    if (sections.stream()
        .map(ParkingSpotSection::parkingSpotId)
        .distinct()
        .count() > 1) {
      throw new IllegalStateException("sections must be for the same parking spot");
    }
  }

  static ParkingSpotSectionsGrouped create(ParkingSpotId parkingSpotId, List<ParkingSpotSectionId> sections) {
    List<ParkingSpotSection> createdSections = sections.stream()
        .map(section -> ParkingSpotSection.free(parkingSpotId, section))
        .toList();
    return new ParkingSpotSectionsGrouped(createdSections, 0);
  }

  boolean occupyBy(SpotUnits spotUnits) {
    int freeSpace = (int) sections.stream()
        .filter(ParkingSpotSection::isFree)
        .count();
    return freeSpace - reservedSpace >= spotUnits.value();
  }

  ParkingSpotId id() {
    return sections.getFirst().parkingSpotId();
  }

}
