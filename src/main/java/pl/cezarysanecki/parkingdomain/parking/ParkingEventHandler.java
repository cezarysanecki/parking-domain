package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientRegistered;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.reservation.ReservationsRemoved;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationUsed;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationsActivated;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
class ParkingEventHandler {

  private final ParkingRepository parkingRepository;
  private final OccupantRepository occupantRepository;
  private final ActiveReservationRepository parkingSpotReservationRepository;

  @EventListener
  public void handle(ParkingSpotAdded event) {
    log.debug("storing parking spot for occupation with id {}", event.parkingSpotId());
    parkingRepository.saveNew(ParkingSpot.create(event.parkingSpotId(), event.capacity()));
  }

  @EventListener
  public void handle(ClientRegistered event) {
    log.debug("storing occupant with id {}", event.clientId());
    occupantRepository.saveNew(Occupant.newOne(event.clientId()));
  }

  @EventListener
  public void handle(ReservationsActivated event) {
    event.reservations()
        .forEach(reservation -> parkingSpotReservationRepository.storeFor(
            reservation.parkingSpotId(),
            reservation.reservationId(),
            reservation.reservationOwnerId(),
            reservation.spotUnits()
        ));
  }

  @EventListener
  public void handle(ReservationUsed event) {
    parkingSpotReservationRepository.remove(List.of(event.reservationId()));
  }

  @EventListener
  public void handle(ReservationsRemoved event) {
    parkingSpotReservationRepository.remove(event.reservations());
  }

}
