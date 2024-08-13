package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotAdded;

@Slf4j
@RequiredArgsConstructor
class ParkingEventHandler {

  private final ParkingRepository parkingRepository;

  @EventListener
  public void handle(ParkingSpotAdded event) {
    log.debug("storing parking spot for occupation with id {}", event.parkingSpotId());
    parkingRepository.saveNew(ParkingSpotSectionsGrouped.create(event.parkingSpotId(), event.sections()));
  }

}
