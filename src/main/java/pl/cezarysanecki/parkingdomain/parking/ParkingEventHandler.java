package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientRegistered;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.requesting.api.MadeRequestsValid;

@Slf4j
@RequiredArgsConstructor
class ParkingEventHandler {

  private final ParkingRepository parkingRepository;
  private final OccupantRepository occupantRepository;
  private final ReservationRepository reservationRepository;

  @EventListener
  public void handle(ParkingSpotAdded event) {
    log.debug("storing parking spot for occupation with id {}", event.parkingSpotId());
    parkingRepository.saveNew(ParkingSpotSectionsGrouped.create(event.parkingSpotId(), event.sections()));
  }

  @EventListener
  public void handle(ClientRegistered event) {
    log.debug("storing occupant with id {}", event.clientId());
    occupantRepository.saveNew(Occupant.newOne(event.clientId()));
  }

  @EventListener
  public void handle(MadeRequestsValid event) {
    reservationRepository.saveAll(
        event.requests()
            .stream()
            .map(request -> new Reservation(
                new ReservationId(request.requestId().value()),
                new ReservationOwnerId(request.requester().value()),
                request.parkingSpotId(),
                request.timeSlot(),
                request.spotUnits()
            ))
            .toList());
  }

}
