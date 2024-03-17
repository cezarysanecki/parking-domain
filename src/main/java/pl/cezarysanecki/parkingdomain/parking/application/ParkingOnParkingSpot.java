package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
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
            ParkingSpot parkingSpot = load(command.getParkingSpotId());
            Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(command.getVehicle());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to park vehicle", throwable));
    }

    public Try<Result> park(@NonNull ParkReservedVehicleCommand command) {
        return Try.of(() -> {
            ParkingSpot parkingSpot = load(command.getReservationId());
            Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(command.getReservationId(), command.getVehicle());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to park vehicle", throwable));
    }

    private Result publishEvents(VehicleParkedEvents vehicleParked) {
        parkingSpots.publish(vehicleParked);
        log.debug("successfully parked vehicle with id {}", vehicleParked.getVehicleParked().getVehicle().getVehicleId());
        return Success;
    }

    private Result publishEvents(ParkingFailed parkingFailed) {
        parkingSpots.publish(parkingFailed);
        log.debug("rejected to park vehicle with id {}, reason: {}", parkingFailed.getVehicleId(), parkingFailed.getReason());
        return Rejection;
    }

    private ParkingSpot load(ReservationId reservationId) {
        return parkingSpots.findBy(reservationId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find parking spot for reservation with id: " + reservationId));
    }

    private ParkingSpot load(ParkingSpotId parkingSpotId) {
        return parkingSpots.findBy(parkingSpotId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find parking spot with id: " + parkingSpotId));
    }

}
