package pl.cezarysanecki.parkingdomain.parkingspot.parking.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.OccupiedParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.vehicle.parking.VehicleId;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

@Slf4j
@RequiredArgsConstructor
public class ReleasingParkingSpot {

    private final ParkingSpots parkingSpots;

    @Value
    public static class Command {

        @NonNull VehicleId vehicleId;

    }

    public Try<Result> driveAway(Command command) {
        VehicleId vehicleId = command.vehicleId;

        return Try.of(() -> {
            OccupiedParkingSpot occupiedParkingSpot = load(vehicleId);
            Either<ParkingSpotEvent.ParkingSpotLeavingOutFailed, ParkingSpotEvent.ParkingSpotLeftEvents> result = occupiedParkingSpot.driveAway(vehicleId);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents)
            );
        }).onFailure(t -> log.error("Failed to occupy parking spot", t));
    }

    private Result publishEvents(ParkingSpotEvent.ParkingSpotLeavingOutFailed parkingSpotLeavingOutFailed) {
        log.debug("failed to leave parking spot with id {}, reason: {}", parkingSpotLeavingOutFailed.getParkingSpotId(), parkingSpotLeavingOutFailed.getReason());
        parkingSpots.publish(parkingSpotLeavingOutFailed);
        return Result.Rejection.with(parkingSpotLeavingOutFailed.getReason());
    }

    private Result publishEvents(ParkingSpotEvent.ParkingSpotLeftEvents parkingSpotLeftEvents) {
        log.debug("successfully left parking spot with id {}", parkingSpotLeftEvents.getParkingSpotId());
        parkingSpots.publish(parkingSpotLeftEvents);
        return new Result.Success();
    }

    private OccupiedParkingSpot load(VehicleId vehicleId) {
        return parkingSpots.findOccupiedBy(vehicleId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot occupied by vehicle with id " + vehicleId));
    }

}
