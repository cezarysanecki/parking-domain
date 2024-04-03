package pl.cezarysanecki.parkingdomain.reservation.client.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsRepository;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ReservationId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationRequestCancelled;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequest {

    private final ClientReservationsRepository clientReservationsRepository;

    @Value
    public class Command {

        @NonNull ReservationId reservationId;

    }

    public Try<Result> cancelReservationRequest(Command command) {
        ReservationId reservationId = command.getReservationId();

        return Try.of(() ->
                clientReservationsRepository.findBy(reservationId)
                        .map(clientReservations -> {
                            Either<ReservationRequestCancellationFailed, ReservationRequestCancelled> result = clientReservations.cancel(reservationId);
                            return Match(result).of(
                                    Case($Left($()), this::publishEvents),
                                    Case($Right($()), this::publishEvents));
                        })
                        .getOrElse(() -> {
                            log.error("cannot find client reservations containing reservation with id {}", reservationId);
                            return Result.Rejection.with("cannot find client reservations");
                        })
        ).onFailure(t -> log.error("Failed to cancel reservation request", t));
    }

    private Result publishEvents(ReservationRequestCancelled requestCancelled) {
        clientReservationsRepository.publish(requestCancelled);
        log.debug("reservation request cancelled for client with id {}", requestCancelled.getClientId());
        return new Result.Success();
    }

    private Result publishEvents(ReservationRequestCancellationFailed requestCancellationFailed) {
        clientReservationsRepository.publish(requestCancellationFailed);
        log.debug("reservation request cancellation failed for client with id {}, reason: {}",
                requestCancellationFailed.getClientId(), requestCancellationFailed.getReason());
        return Result.Rejection.with(requestCancellationFailed.getReason());
    }

}
