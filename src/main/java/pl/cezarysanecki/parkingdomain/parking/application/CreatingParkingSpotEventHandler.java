package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;

@Slf4j
@RequiredArgsConstructor
public class CreatingParkingSpotEventHandler {

  private final ParkingSpotRepository parkingSpotRepository;

  @EventListener
  public void handle(ParkingSpotAdded event) {
    ParkingSpotId parkingSpotId = event.parkingSpotId();
    log.debug("storing parking spot as placement with id: {}", parkingSpotId);
    parkingSpotRepository.saveNew(parkingSpotId, event.capacity(), event.category());
  }

}
