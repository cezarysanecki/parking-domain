package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.model.NormalParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents;

@Slf4j
@RequiredArgsConstructor
public class ParkingOnParkingSpot {

    private final ParkingSpots parkingSpots;

    public Try<Result> park(@NonNull ParkVehicleCommand command) {
        return Try.of(() -> {
            NormalParkingSpot parkingSpot = load(command.getParkingSpotId());
            Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(command.getVehicle());
            return API.Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to park vehicle", throwable));
    }

    private Result publishEvents(VehicleParkedEvents vehicleParked) {
        parkingSpots.publish(vehicleParked);
        return Success;
    }

    private Result publishEvents(ParkingFailed parkingFailed) {
        parkingSpots.publish(parkingFailed);
        return Rejection;
    }

    private NormalParkingSpot load(ParkingSpotId parkingSpotId) {
        return parkingSpots.findBy(parkingSpotId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find parking spot with id: " + parkingSpotId));
    }

}
