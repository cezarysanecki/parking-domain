package pl.cezarysanecki.parkingdomain.parkingspot.parking.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.OpenParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.vehicle.model.VehicleInformation;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

@Slf4j
@RequiredArgsConstructor
public class OccupyingParkingSpot {

    private final ParkingSpots parkingSpots;

    @Value
    public static class Command {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleInformation vehicle;

    }

    public Try<Result> occupy(Command command) {
        ParkingSpotId parkingSpotId = command.parkingSpotId;
        VehicleInformation vehicle = command.vehicle;

        return Try.of(() -> {
            OpenParkingSpot openParkingSpot = load(parkingSpotId);
            Either<ParkingSpotEvent.ParkingSpotOccupationFailed, ParkingSpotEvent.ParkingSpotOccupiedEvents> result = openParkingSpot.occupy(vehicle);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents)
            );
        }).onFailure(t -> log.error("Failed to occupy parking spot", t));
    }

    private Result publishEvents(ParkingSpotEvent.ParkingSpotOccupationFailed parkingSpotOccupationFailed) {
        log.debug("failed to occupy parking spot with id {}, reason: {}", parkingSpotOccupationFailed.getParkingSpotId(), parkingSpotOccupationFailed.getReason());
        parkingSpots.publish(parkingSpotOccupationFailed);
        return Result.Rejection.with(parkingSpotOccupationFailed.getReason());
    }

    private Result publishEvents(ParkingSpotEvent.ParkingSpotOccupiedEvents parkingSpotOccupiedEvents) {
        log.debug("successfully occupied parking spot with id {}", parkingSpotOccupiedEvents.getParkingSpotId());
        parkingSpots.publish(parkingSpotOccupiedEvents);
        return new Result.Success();
    }

    private OpenParkingSpot load(ParkingSpotId parkingSpotId) {
        return parkingSpots.findOpenBy(parkingSpotId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id " + parkingSpotId));
    }

}
