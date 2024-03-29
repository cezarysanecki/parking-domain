package pl.cezarysanecki.parkingdomain.parking.parkingspot.application;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots;

@Slf4j
@RequiredArgsConstructor
public class CreatingParkingSpot {

    private final ParkingSpots parkingSpots;

    @Value
    public static class Command {

        @NonNull ParkingSpotCapacity parkingSpotCapacity;
    }

    public Try<Result> create(Command command) {
        return Try.<Result>of(() -> {
            parkingSpots.publish(new ParkingSpotCreated(ParkingSpotId.newOne(), command.parkingSpotCapacity));
            return new Result.Success();
        }).onFailure(t -> log.error("Failed to create parking spot", t));
    }

    @Value
    public static class ParkingSpotCreated implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ParkingSpotCapacity parkingSpotCapacity;
    }

}
