package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
class InMemoryParkingSpotRepository implements ParkingSpotRepository {

  static final Map<ParkingSpotSectionId, ParkingSpotSection> DATABASE = new ConcurrentHashMap<>();

  @Override
  public void saveNew(ParkingSpotSectionsGrouped parkingSpotSectionsGrouped) {
    parkingSpotSectionsGrouped.sections()
        .forEach(section -> DATABASE.put(section.getSectionId(), section));
  }

  @Override
  public void saveCheckingVersion(ParkingSpotSectionsGrouped parkingSpotSectionsGrouped) {
    saveNew(parkingSpotSectionsGrouped);
  }

  @Override
  public ParkingSpotSectionsGrouped loadFreeSectionsBy(ParkingSpotId parkingSpotId) {
    return new ParkingSpotSectionsGrouped(
        DATABASE.values()
            .stream()
            .filter(section -> section.getParkingSpotId().equals(parkingSpotId)
                && section.getOccupiedBy() == null)
            .toList());
  }

  @Override
  public ParkingSpotSectionsGrouped loadOccupiedSectionsBy(Occupant occupant) {
    return new ParkingSpotSectionsGrouped(
        DATABASE.values()
            .stream()
            .filter(section -> section.getOccupiedBy().equals(occupant))
            .toList());
  }

  @Override
  public ParkingSpotSectionsGrouped loadBy(ParkingSpotId parkingSpotId) {
    return new ParkingSpotSectionsGrouped(
        DATABASE.values()
            .stream()
            .filter(section -> section.getParkingSpotId().equals(parkingSpotId))
            .toList());
  }

}
