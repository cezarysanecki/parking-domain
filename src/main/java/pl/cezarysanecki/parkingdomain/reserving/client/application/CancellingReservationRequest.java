package pl.cezarysanecki.parkingdomain.reserving.client.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservations;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsRepository;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationRequestCancelled;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequest {

    private final ClientReservationsRepository clientReservationsRepository;

    @Value
    public static class Command {

        @NonNull ReservationId reservationId;

    }

    public Try<Result> cancelReservationRequest(Command command) {
        ReservationId reservationId = command.getReservationId();

        return Try.of(() -> {
            ClientReservations clientReservations = load(reservationId);
            Either<ReservationRequestCancellationFailed, ReservationRequestCancelled> result = clientReservations.cancel(reservationId);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(t -> log.error("Failed to cancel reservation request", t));
    }

    private Result publishEvents(ReservationRequestCancelled requestCancelled) {
        log.debug("reservation request cancelled for client with id {}", requestCancelled.getClientId());
        clientReservationsRepository.publish(requestCancelled);
        return new Result.Success();
    }

    private Result publishEvents(ReservationRequestCancellationFailed requestCancellationFailed) {
        log.debug("reservation request cancellation failed for client with id {}, reason: {}",
                requestCancellationFailed.getClientId(), requestCancellationFailed.getReason());
        clientReservationsRepository.publish(requestCancellationFailed);
        return Result.Rejection.with(requestCancellationFailed.getReason());
    }

    private ClientReservations load(ReservationId reservationId) {
        return clientReservationsRepository.findBy(reservationId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find client reservations for reservation with id " + reservationId));
    }

}
