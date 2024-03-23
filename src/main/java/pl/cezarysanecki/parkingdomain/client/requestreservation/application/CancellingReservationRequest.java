package pl.cezarysanecki.parkingdomain.client.requestreservation.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.CancellationOfReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequest {

    private final ClientReservationRequestsRepository clientReservationRequestsRepository;

    public Try<Result> cancelRequest(@NonNull CancelReservationRequestCommand command) {
        return Try.of(() -> {
            ClientReservationRequests clientReservationRequests = load(command.getClientId());
            Either<CancellationOfReservationRequestFailed, ReservationRequestCancelled> result = clientReservationRequests.cancel(command.getReservationId());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve parking slot", throwable));
    }

    private Result publishEvents(ReservationRequestCancelled reservationRequestCancelled) {
        clientReservationRequestsRepository.publish(reservationRequestCancelled);
        log.debug("successfully cancelled reservation request for client with id {}", reservationRequestCancelled.getClientId());
        return Success;
    }

    private Result publishEvents(CancellationOfReservationRequestFailed cancellationOfReservationRequestFailed) {
        clientReservationRequestsRepository.publish(cancellationOfReservationRequestFailed);
        log.debug("rejected to cancel reservation request for client with id {}, reason: {}",
                cancellationOfReservationRequestFailed.getClientId(), cancellationOfReservationRequestFailed.getReason());
        return Rejection;
    }

    private ClientReservationRequests load(ClientId clientId) {
        return clientReservationRequestsRepository.findBy(clientId);
    }

}
