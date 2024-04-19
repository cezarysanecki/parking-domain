package pl.cezarysanecki.parkingdomain.requestingreservation.client.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationRequestCancelled;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequest {

    private final ClientReservationRequestsRepository clientReservationRequestsRepository;

    @Value
    public static class Command {

        @NonNull ReservationId reservationId;

    }

    public Try<Result> cancelReservationRequest(Command command) {
        ReservationId reservationId = command.getReservationId();

        return Try.of(() -> {
            ClientReservationRequests clientReservationRequests = load(reservationId);
            Either<ReservationRequestCancellationFailed, ReservationRequestCancelled> result = clientReservationRequests.cancel(reservationId);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(t -> log.error("Failed to cancel reservation request", t));
    }

    private Result publishEvents(ReservationRequestCancelled requestCancelled) {
        log.debug("reservation request cancelled for client with id {}", requestCancelled.getClientId());
        clientReservationRequestsRepository.publish(requestCancelled);
        return new Result.Success<>(requestCancelled.getClientId());
    }

    private Result publishEvents(ReservationRequestCancellationFailed requestCancellationFailed) {
        log.debug("reservation request cancellation failed for client with id {}, reason: {}",
                requestCancellationFailed.getClientId(), requestCancellationFailed.getReason());
        clientReservationRequestsRepository.publish(requestCancellationFailed);
        return Result.Rejection.with(requestCancellationFailed.getReason());
    }

    private ClientReservationRequests load(ReservationId reservationId) {
        return clientReservationRequestsRepository.findBy(reservationId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find client reservations for reservation with id " + reservationId));
    }

}
