package pl.cezarysanecki.parkingdomain.occupationreleasenotification;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotOccupied;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotReleased;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationsActivated;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class OccupationReleaseNotificationEventHandler {

  private final OccupationReleaseNotificationRepository occupationReleaseNotificationRepository;

  @Transactional
  @EventListener
  public void handle(ParkingSpotAdded event) {
    occupationReleaseNotificationRepository.saveNew(event.parkingSpotId(), event.capacity());
  }

  @Transactional
  @EventListener
  public void handle(ParkingSpotOccupied event) {
    occupationReleaseNotificationRepository.addOccupation(
        event.occupationId(),
        event.occupantId(),
        event.parkingSpotId(),
        event.spotUnits());
  }

  @Transactional
  @EventListener
  public void handle(ParkingSpotReleased event) {
    occupationReleaseNotificationRepository.removeOccupation(event.occupationId());
  }

  @Transactional
  @EventListener
  public void handle(ReservationsActivated event) {
    event.reservations()
        .forEach(reservation -> occupationReleaseNotificationRepository.saveReservation(
            reservation.reservationId(),
            reservation.reservationOwnerId(),
            reservation.parkingSpotId(),
            reservation.startDate(),
            reservation.spotUnits()
        ));
  }

}
