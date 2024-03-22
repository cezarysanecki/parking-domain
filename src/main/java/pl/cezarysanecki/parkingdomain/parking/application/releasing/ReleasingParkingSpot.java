package pl.cezarysanecki.parkingdomain.parking.application.releasing;

import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeftEvents;

@Slf4j
@RequiredArgsConstructor
public class ReleasingParkingSpot {

    private final ParkingSpots parkingSpots;

    public Try<Result> release(@NonNull ReleaseParkingSpotCommand command) {
        return Try.of(() -> {
            ParkingSpot parkingSpot = find(command.getVehicleId());
            Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.driveAway(command.getVehicleId());
            return API.Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to park vehicle", throwable));
    }

    private Result publishEvents(ReleasingFailed releasingFailed) {
        parkingSpots.publish(releasingFailed);
        log.debug("rejected to leave vehicle with ids {}, reason: {}", releasingFailed.getVehicleIds(), releasingFailed.getReason());
        return Rejection;
    }

    private Result publishEvents(VehicleLeftEvents vehicleLeftEvents) {
        parkingSpots.publish(vehicleLeftEvents);
        List<VehicleId> vehicleIds = vehicleLeftEvents.getVehiclesLeft().map(ParkingSpotEvent.VehicleLeft::getVehicle).map(Vehicle::getVehicleId);
        log.debug("successfully vehicle left with ids {}", vehicleIds);
        return Success;
    }

    private ParkingSpot find(VehicleId vehicleId) {
        return parkingSpots.findBy(vehicleId)
                .getOrElseThrow(() -> new IllegalArgumentException("cannot find parking spot for vehicle with id " + vehicleId));
    }

}
