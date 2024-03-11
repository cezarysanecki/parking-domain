package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.model.NormalParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft;

@Slf4j
@RequiredArgsConstructor
public class ReleasingParkingSpot {

    private final ParkingSpots parkingSpots;

    public Try<Result> release(@NonNull ReleaseParkingSpotCommand command) {
        return Try.of(() -> {
            NormalParkingSpot parkingSpot = find(command.getParkingSpotId());
            Either<ReleasingFailed, VehicleLeft> result = parkingSpot.release(command.getVehicleId());
            return API.Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to park vehicle", throwable));
    }

    private Result publishEvents(VehicleLeft vehicleLeft) {
        parkingSpots.publish(vehicleLeft);
        return Success;
    }

    private Result publishEvents(ReleasingFailed releasingFailed) {
        parkingSpots.publish(releasingFailed);
        return Rejection;
    }

    private NormalParkingSpot find(ParkingSpotId parkingSpotId) {
        return parkingSpots.findBy(parkingSpotId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find parking spot with id: " + parkingSpotId));
    }

}
