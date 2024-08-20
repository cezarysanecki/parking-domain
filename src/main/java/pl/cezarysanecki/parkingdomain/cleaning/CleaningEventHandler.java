package pl.cezarysanecki.parkingdomain.cleaning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotReleased;

@Slf4j
@RequiredArgsConstructor
class CleaningEventHandler {

  private final CleaningRepository cleaningRepository;

  @TransactionalEventListener
  public void handle(ParkingSpotReleased event) {
    log.debug("handling parking spot with id {} released to request cleaning", event.parkingSpotId());

    cleaningRepository.increaseCounterFor(event.parkingSpotId());
  }

}
