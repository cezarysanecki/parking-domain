package pl.cezarysanecki.parkingdomain.management.parkingspot;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ParkingSpotFacade {

  private final ParkingSpotRepository database;
  private final EventPublisher eventPublisher;

  public Optional<ParkingSpotId> addParkingSpot(ParkingSpotCapacity capacity, ParkingSpotCategory category) {
    try {
      ParkingSpot parkingSpot = new ParkingSpot(ParkingSpotId.newOne(), capacity, category);
      log.debug("adding parking spot with id {}", parkingSpot.parkingSpotId());

      database.saveNew(parkingSpot);

      eventPublisher.publish(new ParkingSpotAdded(parkingSpot.parkingSpotId(), capacity));

      return Optional.of(parkingSpot.parkingSpotId());
    } catch (Exception exception) {
      log.error("failed to add parking spot", exception);
      return Optional.empty();
    }
  }

}
