package pl.cezarysanecki.parkingdomain.cleaning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotReleased;

@Slf4j
@RequiredArgsConstructor
class CleaningEventHandler {

  private final CleaningRepository cleaningRepository;

  @Transactional
  @EventListener
  public void handle(ParkingSpotReleased event) {
    log.debug("handling parking spot with id {} released to request cleaning", event.parkingSpotId());

    cleaningRepository.increaseCounterFor(event.parkingSpotId());
  }

}
