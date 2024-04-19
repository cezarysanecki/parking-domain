package pl.cezarysanecki.parkingdomain.requestingreservation.client.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForWholeParkingSpotRequested;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.RequestingReservationFailed;

@Slf4j
@RequiredArgsConstructor
public class RequestingReservationForWholeParkingSpot {

    private final ClientReservationRequestsRepository clientReservationRequestsRepository;

    @Value
    public static class Command {

        @NonNull ClientId clientId;
        @NonNull ParkingSpotId parkingSpotId;

    }

    public Try<Result> requestReservation(Command command) {
        ClientId clientId = command.getClientId();
        ParkingSpotId parkingSpotId = command.getParkingSpotId();

        return Try.of(() -> {
            ClientReservationRequests clientReservationRequests = load(clientId);
            Either<RequestingReservationFailed, ReservationForWholeParkingSpotRequested> result = clientReservationRequests.createRequest(parkingSpotId);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(t -> log.error("Failed to place a hold", t));
    }

    private Result publishEvents(ReservationForWholeParkingSpotRequested requestSubmitted) {
        log.debug("reservation for whole parking spot submitted for client with id {}", requestSubmitted.getClientId());
        clientReservationRequestsRepository.publish(requestSubmitted);
        return new Result.Success<>(requestSubmitted.getReservationId());
    }

    private Result publishEvents(RequestingReservationFailed requestSubmissionFailed) {
        log.debug("reservation request submission failed for client with id {}, reason: {}",
                requestSubmissionFailed.getClientId(), requestSubmissionFailed.getReason());
        clientReservationRequestsRepository.publish(requestSubmissionFailed);
        return Result.Rejection.with(requestSubmissionFailed.getReason());
    }

    private ClientReservationRequests load(ClientId clientId) {
        return clientReservationRequestsRepository.findBy(clientId)
                .getOrElse(() -> ClientReservationRequests.empty(clientId));
    }

}
