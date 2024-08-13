package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;

import java.util.List;
import java.util.stream.IntStream;

record ParkingSpotSectionsGrouped(
    List<ParkingSpotSection> sections
) {

  private static final int DEFAULT_NUMBER_OF_SEGMENTS = 4;

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

  static ParkingSpotSectionsGrouped create(ParkingSpotId parkingSpotId) {
    List<ParkingSpotSection> sections = IntStream.of(0, DEFAULT_NUMBER_OF_SEGMENTS)
        .mapToObj(index -> ParkingSpotSection.free(parkingSpotId))
        .toList();
    return new ParkingSpotSectionsGrouped(sections);
  }

  boolean occupyBy(OccupationId occupationId) {
    return sections.stream()
        .allMatch(ParkingSpotSection::isFree);
  }

  ParkingSpotId id() {
    return sections.getFirst().parkingSpotId();
  }

}
