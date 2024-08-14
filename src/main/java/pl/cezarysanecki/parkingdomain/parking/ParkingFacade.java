package pl.cezarysanecki.parkingdomain.parking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotReleased;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ParkingFacade {

  private final ParkingRepository parkingRepository;
  private final OccupationRepository occupationRepository;
  private final OccupantRepository occupantRepository;
  private final ReservationRepository reservationRepository;
  private final DateProvider dateProvider;
  private final EventPublisher eventPublisher;

  private final int minutesWhenReservationIsActive;

  @Transactional
  public Optional<OccupationId> occupy(
      OccupantId occupantId,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits
  ) {
    Instant activationDate = dateProvider.now().plus(Duration.ofMinutes(minutesWhenReservationIsActive));

    log.debug("occupying parking spot with id {} by {} units", parkingSpotId, spotUnits);
    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped = parkingRepository.loadBy(parkingSpotId, activationDate);
    Occupant occupant = occupantRepository.findBy(occupantId, activationDate);

    OccupationId occupationId = OccupationId.newOne();
    if (!occupant.canOccupy(occupationId) || !parkingSpotSectionsGrouped.occupyBy(spotUnits)) {
      log.debug("failed to occupy parking spot with id {}", parkingSpotId);
      return Optional.empty();
    }
    occupationRepository.saveCheckingVersion(new Occupation(
        occupationId, occupant, parkingSpotSectionsGrouped, ReservationId.none()
    ));
    return Optional.of(occupationId);
  }

  @Transactional
  public Optional<OccupationId> occupyUsing(
      OccupantId occupantId,
      ReservationId reservationId
  ) {
    Instant activationDate = dateProvider.now().plus(Duration.ofMinutes(minutesWhenReservationIsActive));

    log.debug("occupying parking spot using reservation with id {}", reservationId);
    Reservation reservation = reservationRepository.loadBy(reservationId);

    ParkingSpotSectionsGrouped parkingSpotSectionsGrouped = parkingRepository.loadBy(reservation.parkingSpotId(), activationDate);
    Occupant occupant = occupantRepository.findBy(occupantId, activationDate);

    OccupationId occupationId = OccupationId.newOne();
    if (!occupant.canOccupyWithReservation(reservationId) || !parkingSpotSectionsGrouped.occupyBy(reservation.spotUnits())) {
      log.debug("failed to occupy parking spot with id {}", reservation.parkingSpotId());
      return Optional.empty();
    }
    occupationRepository.saveCheckingVersion(new Occupation(
        occupationId, occupant, parkingSpotSectionsGrouped, reservationId
    ));
    return Optional.of(occupationId);
  }

  @Transactional
  public boolean release(
      OccupationId occupationId
  ) {
    log.debug("releasing occupation with id {}", occupationId);
    Optional<ReleasedOccupation> potentiallyReleasedOccupation = occupationRepository.delete(occupationId);
    if (potentiallyReleasedOccupation.isEmpty()) {
      log.debug("failed to release occupation with id {}", occupationId);
      return false;
    }
    ReleasedOccupation releasedOccupation = potentiallyReleasedOccupation.get();
    log.debug("releasing occupation for parking spot with id {} for {} units", releasedOccupation.parkingSpotId(), releasedOccupation.sections().size());

    eventPublisher.publish(new ParkingSpotReleased(
        releasedOccupation.occupationId(),
        releasedOccupation.occupantId(),
        releasedOccupation.parkingSpotId(),
        releasedOccupation.sections()
    ));
    return true;
  }

}
