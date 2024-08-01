package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

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
        .map(ParkingSpotSection::getParkingSpotId)
        .distinct()
        .count() > 1) {
      throw new IllegalStateException("sections must be for the same parking spot");
    }
  }

  static ParkingSpotSectionsGrouped of(ParkingSpotId parkingSpotId) {
    List<ParkingSpotSection> sections = IntStream.of(0, DEFAULT_NUMBER_OF_SEGMENTS)
        .mapToObj(index -> ParkingSpotSection.free(parkingSpotId))
        .toList();
    return new ParkingSpotSectionsGrouped(sections);
  }

  boolean occupyWhole(Occupant occupant) {
    return sections.stream()
        .allMatch(section -> section.occupy(occupant));
  }

  boolean occupyPart(Occupant occupant, SpotUnits spotUnits) {
    List<ParkingSpotSection> freeSections = sections.stream()
        .filter(section -> section.canBeOccupiedBy(occupant))
        .toList();
    if (freeSections.size() < spotUnits.value()) return false;

    return freeSections.stream()
        .allMatch(section -> section.occupy(occupant));
  }

  boolean release(Occupant occupant) {
    sections.forEach(section -> section.release(occupant));
    return true;
  }

  ParkingSpotId id() {
    return sections.getFirst().getParkingSpotId();
  }

}
