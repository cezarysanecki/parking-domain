package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.commons.commands.ValidationError;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import java.util.Set;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.CancellationOfReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequest {

    private final ClientReservationRequestsRepository clientReservationRequestsRepository;
    private final ClientReservationRequestCommandValidator clientReservationRequestCommandValidator;

    public Try<Result> cancelRequest(@NonNull CancelReservationRequestCommand command) {
        Set<ValidationError> validationErrors = clientReservationRequestCommandValidator.validate(command);
        if (!validationErrors.isEmpty()) {
            return Try.success(new Rejection(validationErrors));
        }

        return Try.of(() -> {
            ClientReservationRequests clientReservationRequests = load(command.getReservationId());
            Either<CancellationOfReservationRequestFailed, ReservationRequestCancelled> result = clientReservationRequests.cancel(command.getReservationId());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve parking slot", throwable));
    }

    private Result publishEvents(ReservationRequestCancelled reservationRequestCancelled) {
        clientReservationRequestsRepository.publish(reservationRequestCancelled);
        log.debug("successfully cancelled reservation request for client with id {}", reservationRequestCancelled.getClientId());
        return new Success();
    }

    private Result publishEvents(CancellationOfReservationRequestFailed cancellationOfReservationRequestFailed) {
        clientReservationRequestsRepository.publish(cancellationOfReservationRequestFailed);
        log.debug("rejected to cancel reservation request for client with id {}, reason: {}",
                cancellationOfReservationRequestFailed.getClientId(), cancellationOfReservationRequestFailed.getReason());
        return Rejection.empty();
    }

    private ClientReservationRequests load(ReservationId reservationId) {
        return clientReservationRequestsRepository.findBy(reservationId)
                .getOrElseThrow(() -> new IllegalArgumentException("cannot find client reservation for reservation id " + reservationId));
    }

}
