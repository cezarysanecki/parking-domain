package pl.cezarysanecki.parkingdomain.parking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotReleased;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.Optional;

@RequiredArgsConstructor
public class ParkingSpotFacade {

  private final ParkingSpotRepository parkingSpotRepository;
  private final OccupationRepository occupationRepository;
  private final EventPublisher eventPublisher;

  @Transactional
  public ParkingSpotId create() {
    ParkingSpotId parkingSpotId = ParkingSpotId.newOne();
    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped = ParkingSpotSectionsGrouped.create(parkingSpotId);
    parkingSpotRepository.saveNew(parkingSpotSectionsGrouped);
    return parkingSpotId;
  }

  @Transactional
  public boolean occupy(
      Occupant occupant,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits
  ) {
    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped = parkingSpotRepository.loadFreeSectionsFor(parkingSpotId, spotUnits);

    OccupationId occupationId = OccupationId.newOne();
    if (!parkingSpotSectionsGrouped.occupyBy(occupationId)) {
      return false;
    }
    occupationRepository.saveCheckingVersion(new Occupation(
        occupationId, occupant, parkingSpotId, parkingSpotSectionsGrouped.sections()
    ));
    return true;
  }

  @Transactional
  public boolean occupyWhole(
      Occupant occupant,
      ParkingSpotId parkingSpotId
  ) {
    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped = parkingSpotRepository.loadBy(parkingSpotId);

    OccupationId occupationId = OccupationId.newOne();
    if (!parkingSpotSectionsGrouped.occupyBy(occupationId)) {
      return false;
    }
    occupationRepository.saveCheckingVersion(new Occupation(
        occupationId, occupant, parkingSpotId, parkingSpotSectionsGrouped.sections()
    ));
    return true;
  }

  @Transactional
  public boolean release(
      OccupationId occupationId
  ) {
    Optional<Occupation> deletedOccupation = occupationRepository.delete(occupationId);
    if (deletedOccupation.isEmpty()) {
      return false;
    }
    Occupation occupation = deletedOccupation.get();
    eventPublisher.publish(new ParkingSpotReleased(
        occupation.occupationId(), occupation.occupant(), occupation.parkingSpotId(),
        occupation.sections().stream().map(ParkingSpotSection::sectionId).toList()
    ));
    return true;
  }

}
