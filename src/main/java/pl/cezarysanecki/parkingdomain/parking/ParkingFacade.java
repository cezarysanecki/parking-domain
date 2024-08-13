package pl.cezarysanecki.parkingdomain.parking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotReleased;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.Optional;

@RequiredArgsConstructor
public class ParkingFacade {

  private final ParkingRepository parkingRepository;
  private final OccupationRepository occupationRepository;
  private final EventPublisher eventPublisher;

  @Transactional
  public boolean occupy(
      Occupant occupant,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits
  ) {
    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped = parkingRepository.loadFreeSectionsFor(parkingSpotId, spotUnits);

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
