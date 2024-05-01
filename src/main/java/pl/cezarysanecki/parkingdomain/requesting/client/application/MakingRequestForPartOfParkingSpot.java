package pl.cezarysanecki.parkingdomain.requesting.client.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.SpotUnits;
import pl.cezarysanecki.parkingdomain.management.client.ClientId;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequests;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.MakingRequestFailed;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForPartOfParkingSpotMade;

@Slf4j
@RequiredArgsConstructor
public class MakingRequestForPartOfParkingSpot {

    private final ClientRequestsRepository clientRequestsRepository;

    @Value
    public static class Command {

        @NonNull
        ClientId clientId;
        @NonNull
        ParkingSpotId parkingSpotId;
        @NonNull
        SpotUnits spotUnits;

    }

    public Try<Result> makeRequest(Command command) {
        ClientId clientId = command.getClientId();
        ParkingSpotId parkingSpotId = command.getParkingSpotId();
        SpotUnits spotUnits = command.getSpotUnits();

        return Try.of(() -> {
            ClientRequests clientRequests = load(clientId);
            Either<MakingRequestFailed, RequestForPartOfParkingSpotMade> result = clientRequests.createRequest(parkingSpotId, spotUnits);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(t -> log.error("Failed to place a hold", t));
    }

    private Result publishEvents(RequestForPartOfParkingSpotMade requestSubmitted) {
        log.debug("request for part of parking spot made for client with id {}", requestSubmitted.getClientId());
        clientRequestsRepository.publish(requestSubmitted);
        return new Result.Success<>(requestSubmitted.getRequestId());
    }

    private Result publishEvents(MakingRequestFailed requestSubmissionFailed) {
        log.debug("making request failed for client with id {}, reason: {}",
                requestSubmissionFailed.getClientId(), requestSubmissionFailed.getReason());
        clientRequestsRepository.publish(requestSubmissionFailed);
        return Result.Rejection.with(requestSubmissionFailed.getReason());
    }

    private ClientRequests load(ClientId clientId) {
        return clientRequestsRepository.findBy(clientId)
                .getOrElse(() -> {
                    log.debug("resolving new client to create request");
                    return ClientRequests.empty(clientId);
                });
    }

}
