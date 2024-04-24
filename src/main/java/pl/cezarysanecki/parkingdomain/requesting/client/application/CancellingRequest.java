package pl.cezarysanecki.parkingdomain.requesting.client.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequests;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled;

@Slf4j
@RequiredArgsConstructor
public class CancellingRequest {

    private final ClientRequestsRepository clientRequestsRepository;

    @Value
    public static class Command {

        @NonNull
        RequestId requestId;

    }

    public Try<Result> cancelRequest(Command command) {
        RequestId requestId = command.getRequestId();

        return Try.of(() -> {
            ClientRequests clientRequests = load(requestId);
            Either<RequestCancellationFailed, RequestCancelled> result = clientRequests.cancel(requestId);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(t -> log.error("Failed to cancel request", t));
    }

    private Result publishEvents(RequestCancelled requestCancelled) {
        log.debug("request cancelled for client with id {}", requestCancelled.getClientId());
        clientRequestsRepository.publish(requestCancelled);
        return new Result.Success<>(requestCancelled.getClientId());
    }

    private Result publishEvents(RequestCancellationFailed requestCancellationFailed) {
        log.debug("request cancellation failed for client with id {}, reason: {}",
                requestCancellationFailed.getClientId(), requestCancellationFailed.getReason());
        clientRequestsRepository.publish(requestCancellationFailed);
        return Result.Rejection.with(requestCancellationFailed.getReason());
    }

    private ClientRequests load(RequestId requestId) {
        return clientRequestsRepository.findBy(requestId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find client requests containing request with id " + requestId));
    }

}
