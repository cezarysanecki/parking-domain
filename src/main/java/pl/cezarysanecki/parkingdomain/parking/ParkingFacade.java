package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotOccupied;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotReleased;
import pl.cezarysanecki.parkingdomain.parking.api.ReleasedOccupation;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.ParkingOpeningHours;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ParkingFacade {

  private final ParkingRepository parkingRepository;
  private final OccupationRepository occupationRepository;
  private final OccupantRepository occupantRepository;
  private final EventPublisher eventPublisher;
  private final DateProvider dateProvider;

  @Transactional
  public Optional<OccupationId> occupy(
      OccupantId occupantId,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits
  ) {
    log.debug("occupying parking spot with id {} by {} units", parkingSpotId, spotUnits);
    if (!canOccupyNow()) {
      log.debug("cannot occupy parking spot with id {} outside occupying hours", parkingSpotId);
      return Optional.empty();
    }
    ParkingSpot parkingSpot = parkingRepository.loadBy(parkingSpotId);
    Occupant occupant = occupantRepository.findBy(occupantId);

    OccupationId occupationId = OccupationId.newOne();
    if (!occupant.canOccupy(occupationId) || !parkingSpot.occupyBy(spotUnits)) {
      log.debug("failed to occupy parking spot with id {}", parkingSpotId);
      return Optional.empty();
    }
    occupationRepository.saveCheckingVersion(new Occupation(
        occupationId, occupant, parkingSpot, spotUnits
    ));

    eventPublisher.publish(new ParkingSpotOccupied(
        occupationId, occupantId, parkingSpotId, spotUnits
    ));
    return Optional.of(occupationId);
  }

  @Transactional
  public Optional<OccupationId> occupyUsing(
      OccupantId occupantId,
      ParkingSpotId parkingSpotId,
      ReservationId reservationId
  ) {
    log.debug("occupying parking spot with id {} with reservation {}", parkingSpotId, reservationId);
    if (!canOccupyNow()) {
      log.debug("cannot occupy parking spot with id {} outside occupying hours", parkingSpotId);
      return Optional.empty();
    }
    ParkingSpot parkingSpot = parkingRepository.loadBy(parkingSpotId);
    Occupant occupant = occupantRepository.findBy(occupantId);

    OccupationId occupationId = OccupationId.newOne();
    Optional<Reservation> parkingSpotResult = parkingSpot.occupyBy(reservationId);
    if (!occupant.canOccupy(occupationId) || parkingSpotResult.isEmpty()) {
      log.debug("failed to occupy parking spot with id {}", parkingSpotId);
      return Optional.empty();
    }

    Reservation reservation = parkingSpotResult.get();
    occupationRepository.saveCheckingVersion(new Occupation(
        occupationId, occupant, parkingSpot, reservation.spotUnits()
    ));

    eventPublisher.publish(new ParkingSpotOccupied(
        occupationId, occupantId, parkingSpotId, reservation.spotUnits()
    ));
    return Optional.of(occupationId);
  }

  @Transactional
  public Optional<ReleasedOccupation> release(
      OccupationId occupationId
  ) {
    log.debug("releasing occupation with id {}", occupationId);
    Optional<ReleasedOccupation> potentiallyReleasedOccupation = occupationRepository.delete(occupationId);
    if (potentiallyReleasedOccupation.isEmpty()) {
      log.debug("failed to release occupation with id {}", occupationId);
      return Optional.empty();
    }
    ReleasedOccupation releasedOccupation = potentiallyReleasedOccupation.get();
    log.debug("releasing occupation for parking spot with id {} for {} units", releasedOccupation.parkingSpotId(), releasedOccupation.spotUnits().value());

    eventPublisher.publish(new ParkingSpotReleased(
        releasedOccupation.occupationId(),
        releasedOccupation.occupantId(),
        releasedOccupation.parkingSpotId(),
        releasedOccupation.spotUnits()
    ));
    return Optional.of(releasedOccupation);
  }

  public boolean canOccupyNow() {
    return ParkingOpeningHours.canOccupyAt(dateProvider.now());
  }

  public List<OccupationId> findAllOccupations() {
    return occupationRepository.findAll();
  }

}
