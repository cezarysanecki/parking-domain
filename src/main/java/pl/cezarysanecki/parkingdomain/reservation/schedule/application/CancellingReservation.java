package pl.cezarysanecki.parkingdomain.reservation.schedule.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSchedule;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleEvent;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSchedules;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservation {

    private final ReservationSchedules reservationSchedules;

    public Try<Result> cancel(@NonNull CancelReservationCommand command) {
        return Try.of(() -> {
            ReservationSchedule reservationSchedule = load(command.getReservationId());
            Either<ReservationScheduleEvent.ReservationCancellationFailed, ReservationScheduleEvent.ReservationCancelled> result = reservationSchedule.cancel(command.getReservationId());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve parking slot", throwable));
    }

    private Result publishEvents(ReservationScheduleEvent.ReservationCancelled reservationCancelled) {
        reservationSchedules.publish(reservationCancelled);
        log.debug("successfully cancelled reservation with id {}", reservationCancelled.getReservationId());
        return Success;
    }

    private Result publishEvents(ReservationScheduleEvent.ReservationCancellationFailed reservationCancellationFailed) {
        reservationSchedules.publish(reservationCancellationFailed);
        log.debug("rejected to cancel reservation with id {}, reason: {}", reservationCancellationFailed.getReservationId(), reservationCancellationFailed.getReason());
        return Rejection;
    }

    private ReservationSchedule load(ReservationId reservationId) {
        return reservationSchedules.findBy(reservationId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find reservation with id: " + reservationId));
    }

}
