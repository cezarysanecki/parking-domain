package pl.cezarysanecki.parkingdomain.reservation.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationSchedule;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationSchedules;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationCancellationFailed;
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationCancelled;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservation {

    private final ReservationSchedules reservationSchedules;

    public Try<Result> cancel(@NonNull CancelReservationCommand command) {
        return Try.of(() -> {
            ReservationSchedule reservationSchedule = load(command.getReservationId());
            Either<ReservationCancellationFailed, ReservationCancelled> result = reservationSchedule.cancel(command.getReservationId());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve parking slot", throwable));
    }

    private Result publishEvents(ReservationCancelled reservationCancelled) {
        reservationSchedules.publish(reservationCancelled);
        return Success;
    }

    private Result publishEvents(ReservationCancellationFailed reservationCancellationFailed) {
        reservationSchedules.publish(reservationCancellationFailed);
        return Rejection;
    }

    private ReservationSchedule load(ReservationId reservationId) {
        return reservationSchedules.findBy(reservationId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));
    }

}
