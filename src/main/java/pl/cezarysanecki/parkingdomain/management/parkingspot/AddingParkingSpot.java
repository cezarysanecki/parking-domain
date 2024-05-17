package pl.cezarysanecki.parkingdomain.management.parkingspot;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AddingParkingSpot {

    private final CatalogueParkingSpotDatabase database;
    private final EventPublisher eventPublisher;

    public Try<ParkingSpotId> addParkingSpot(int capacity, ParkingSpotCategory category) {
        return Try.of(() -> {
            ParkingSpot parkingSpot = new ParkingSpot(UUID.randomUUID(), capacity, category);
            log.debug("adding parking spot with id {}", parkingSpot.getParkingSpotId());

            database.saveNew(parkingSpot);

            eventPublisher.publish(new ParkingSpotAdded(parkingSpot));

            return parkingSpot.getParkingSpotId();
        }).onFailure(t -> log.error("failed to add parking spot", t));
    }

}
