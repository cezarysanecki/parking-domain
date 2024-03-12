package pl.cezarysanecki.parkingdomain.reservation.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationSchedule;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationSchedules;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationFailed;
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationMade;

@Slf4j
@RequiredArgsConstructor
public class MakingParkingSlotReservation {

    private final ReservationSchedules reservationSchedules;

    public Try<Result> reserve(@NonNull ReserveParkingSpotCommand command) {
        return Try.of(() -> {
            ReservationSchedule reservationSchedule = reservationSchedules.findBy(command.getParkingSpotId());
            Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(command.getVehicles(), command.getReservationSlot());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve parking slot", throwable));
    }

    public Try<Result> reserve(@NonNull ReserveAnyParkingSpotCommand command) {
        return Try.of(() -> {
            ReservationSchedule reservationSchedule = reservationSchedules.findFreeFor(command.getReservationSlot());
            Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(command.getVehicles(), command.getReservationSlot());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve parking slot", throwable));
    }

    private Result publishEvents(ReservationMade reservationMade) {
        reservationSchedules.publish(reservationMade);
        return Success;
    }

    private Result publishEvents(ReservationFailed reservationFailed) {
        reservationSchedules.publish(reservationFailed);
        return Rejection;
    }

}
