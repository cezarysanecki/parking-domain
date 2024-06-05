package pl.cezarysanecki.parkingdomain.cleaning.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.cleaning.model.CleaningRepository;
import pl.cezarysanecki.parkingdomain.parking.integration.ParkingSpotReleased;

@Slf4j
@RequiredArgsConstructor
public class CountingReleasedOccupationsEventHandler {

  private final CleaningRepository cleaningRepository;

  @EventListener
  public void handle(ParkingSpotReleased event) {
    log.debug("handling parking spot with id {} released to request cleaning", event.parkingSpotId());

    cleaningRepository.increaseCounterFor(event.parkingSpotId());
  }

}
