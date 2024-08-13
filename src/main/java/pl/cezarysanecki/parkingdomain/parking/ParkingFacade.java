package pl.cezarysanecki.parkingdomain.parking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotReleased;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.Optional;

@Slf4j
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
    log.debug("occupying parking spot with id {} by {} units", parkingSpotId, spotUnits);
    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped = parkingRepository.loadFreeSectionsFor(parkingSpotId, spotUnits);

    OccupationId occupationId = OccupationId.newOne();
    if (!parkingSpotSectionsGrouped.occupyBy(occupationId)) {
      log.debug("failed to occupy parking spot with id {}", parkingSpotId);
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
    log.debug("releasing occupation with id {}", occupationId);
    Optional<Occupation> deletedOccupation = occupationRepository.delete(occupationId);
    if (deletedOccupation.isEmpty()) {
      log.debug("failed to release occupation with id {}", occupationId);
      return false;
    }
    Occupation occupation = deletedOccupation.get();
    log.debug("releasing occupation for parking spot with id {} for {} units", occupation.parkingSpotId(), occupation.sections().size());

    eventPublisher.publish(new ParkingSpotReleased(
        occupation.occupationId(), occupation.occupant(), occupation.parkingSpotId(),
        occupation.sections().stream().map(ParkingSpotSection::sectionId).toList()
    ));
    return true;
  }

}
