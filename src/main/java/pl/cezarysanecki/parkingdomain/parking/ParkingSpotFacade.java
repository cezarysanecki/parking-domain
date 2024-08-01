package pl.cezarysanecki.parkingdomain.parking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotReleased;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@RequiredArgsConstructor
public class ParkingSpotFacade {

  private final ParkingSpotRepository parkingSpotRepository;
  private final EventPublisher eventPublisher;

  @Transactional
  public ParkingSpotId create() {
    ParkingSpotId parkingSpotId = ParkingSpotId.newOne();
    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped = ParkingSpotSectionsGrouped.of(parkingSpotId);
    parkingSpotRepository.saveNew(parkingSpotSectionsGrouped);
    return parkingSpotId;
  }

  @Transactional
  public boolean occupy(
      Occupant occupant,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits
  ) {
    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped = parkingSpotRepository.loadFreeSectionsBy(parkingSpotId);
    if (!parkingSpotSectionsGrouped.occupyPart(occupant, spotUnits)) {
      return false;
    }
    parkingSpotRepository.saveCheckingVersion(parkingSpotSectionsGrouped);
    return true;
  }

  @Transactional
  public boolean occupyWhole(
      Occupant occupant,
      ParkingSpotId parkingSpotId
  ) {
    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped = parkingSpotRepository.loadBy(parkingSpotId);
    if (!parkingSpotSectionsGrouped.occupyWhole(occupant)) {
      return false;
    }
    parkingSpotRepository.saveCheckingVersion(parkingSpotSectionsGrouped);
    return true;
  }

  @Transactional
  public boolean release(
      Occupant occupant
  ) {
    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped = parkingSpotRepository.loadOccupiedSectionsBy(occupant);
    if (!parkingSpotSectionsGrouped.release(occupant)) {
      return false;
    }
    parkingSpotRepository.saveCheckingVersion(parkingSpotSectionsGrouped);
    eventPublisher.publish(new ParkingSpotReleased(parkingSpotSectionsGrouped.id()));
    return true;
  }

}
