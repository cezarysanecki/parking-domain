package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;

import java.util.List;

record ParkingSpotSectionsGrouped(
    List<ParkingSpotSection> sections
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
    return new ParkingSpotSectionsGrouped(createdSections);
  }

  boolean occupyBy(OccupationId occupationId) {
    return sections.stream()
        .allMatch(ParkingSpotSection::isFree);
  }

  ParkingSpotId id() {
    return sections.getFirst().parkingSpotId();
  }

}
