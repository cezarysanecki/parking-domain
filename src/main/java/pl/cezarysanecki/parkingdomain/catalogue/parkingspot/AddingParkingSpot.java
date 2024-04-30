package pl.cezarysanecki.parkingdomain.catalogue.parkingspot;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Slf4j
@RequiredArgsConstructor
public class AddingParkingSpot {

    private final CatalogueParkingSpotDatabase database;
    private final EventPublisher eventPublisher;

    public Try<Result> addParkingSpot(int capacity, ParkingSpotCategory category) {
        return Try.<Result>of(() -> {
            ParkingSpot parkingSpot = new ParkingSpot(ParkingSpotId.newOne(), ParkingSpotCapacity.of(capacity), category);
            log.debug("adding parking spot with id {}", parkingSpot.getParkingSpotId());

            database.saveNew(parkingSpot);

            eventPublisher.publish(new ParkingSpotAdded(parkingSpot));

            return new Result.Success<>(parkingSpot.getParkingSpotId());
        }).onFailure(t -> log.error("failed to add parking spot", t));
    }

}
