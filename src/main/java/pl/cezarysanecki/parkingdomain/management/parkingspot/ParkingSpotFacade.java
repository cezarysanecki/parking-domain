package pl.cezarysanecki.parkingdomain.management.parkingspot;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ParkingSpotFacade {

  private final ParkingSpotRepository database;
  private final EventPublisher eventPublisher;

  public Try<ParkingSpotId> addParkingSpot(ParkingSpotCapacity capacity, ParkingSpotCategory category) {
    return Try.of(() -> {
      ParkingSpot parkingSpot = new ParkingSpot(ParkingSpotId.newOne(), capacity, category);
      log.debug("adding parking spot with id {}", parkingSpot.parkingSpotId());

      database.saveNew(parkingSpot);

      eventPublisher.publish(new ParkingSpotAdded(parkingSpot.parkingSpotId(), capacity));

      return parkingSpot.parkingSpotId();
    }).onFailure(t -> log.error("failed to add parking spot", t));
  }

}
