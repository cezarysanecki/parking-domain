package pl.cezarysanecki.parkingdomain.managment;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

import java.util.UUID;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ParkingManagement {

    private final EventPublisher eventPublisher;

    public Try<Result> addParkingSpot() {
        return Try.of(() -> {
            UUID parkingSpotId = UUID.randomUUID();
            log.debug("Created parking spot with id {}", parkingSpotId);

            eventPublisher.publish(new CreatedNewParkingSpot(parkingSpotId));
            return Result.Success;
        });
    }

}

