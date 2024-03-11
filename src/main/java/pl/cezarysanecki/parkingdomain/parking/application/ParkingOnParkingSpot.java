package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.model.NormalParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFulfilled;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parking.model.ReservedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;

import java.time.Instant;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static io.vavr.Predicates.instanceOf;
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
            ParkingSpot parkingSpot = load(command.getParkingSpotId(), command.getWhen());

            return Match(parkingSpot).of(
                    Case($(instanceOf(NormalParkingSpot.class)), normalParkingSpot -> handleForNormalSpot(normalParkingSpot, command.getVehicle())),
                    Case($(instanceOf(ReservedParkingSpot.class)), reservedParkingSpot -> handleForReservedSpot(reservedParkingSpot, command.getVehicle())),
                    Case($(), () -> Success)
            );
        }).onFailure(throwable -> log.error("Failed to park vehicle", throwable));
    }

    private Result handleForNormalSpot(NormalParkingSpot parkingSpot, Vehicle vehicle) {
        Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(vehicle);
        return Match(result).of(
                Case($Left($()), this::publishEvents),
                Case($Right($()), this::publishEvents));
    }

    private Result handleForReservedSpot(ReservedParkingSpot parkingSpot, Vehicle vehicle) {
        Either<ParkingFailed, ReservationFulfilled> result = parkingSpot.park(vehicle);
        return Match(result).of(
                Case($Left($()), this::publishEvents),
                Case($Right($()), this::publishEvents));
    }

    private Result publishEvents(VehicleParkedEvents vehicleParked) {
        parkingSpots.publish(vehicleParked);
        return Success;
    }

    private Result publishEvents(ReservationFulfilled reservationFulfilled) {
        parkingSpots.publish(reservationFulfilled);
        return Success;
    }

    private Result publishEvents(ParkingFailed parkingFailed) {
        parkingSpots.publish(parkingFailed);
        return Rejection;
    }

    private ParkingSpot load(ParkingSpotId parkingSpotId, Instant when) {
        return parkingSpots.findBy(parkingSpotId, when)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find parking spot with id: " + parkingSpotId));
    }

}
