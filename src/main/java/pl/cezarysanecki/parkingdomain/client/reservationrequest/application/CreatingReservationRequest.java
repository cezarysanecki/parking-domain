package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFactory;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.AnyParkingSpotReservationRequested;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ChosenParkingSpotReservationRequested;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;

@Slf4j
@RequiredArgsConstructor
public class CreatingReservationRequest {

    private final ClientReservationRequestsRepository clientReservationRequestsRepository;
    private final ClientReservationRequestsFactory clientReservationRequestsFactory;

    public Try<Result> createRequest(@NonNull CreateReservationRequestForChosenParkingSpotCommand command) {
        return Try.of(() -> {
            ClientReservationRequests clientReservationRequests = load(command.getClientId());
            Either<ReservationRequestFailed, ChosenParkingSpotReservationRequested> result = clientReservationRequests.reserve(
                    command.getReservationPeriod(),
                    command.getParkingSpotId());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to create reservation request", throwable));
    }

    public Try<Result> createRequest(@NonNull CreateReservationRequestForAnyPartOfParkingSpotCommand command) {
        return Try.of(() -> {
            ClientReservationRequests clientReservationRequests = load(command.getClientId());
            Either<ReservationRequestFailed, AnyParkingSpotReservationRequested> result = clientReservationRequests.reserve(
                    command.getReservationPeriod(),
                    command.getParkingSpotType(),
                    command.getVehicleSizeUnit());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to create reservation request", throwable));
    }

    private Result publishEvents(ChosenParkingSpotReservationRequested chosenParkingSpotReservationRequested) {
        clientReservationRequestsRepository.publish(chosenParkingSpotReservationRequested);
        log.debug("successfully created reservation request for client with id {}", chosenParkingSpotReservationRequested.getClientId());
        return Success;
    }

    private Result publishEvents(AnyParkingSpotReservationRequested anyParkingSpotReservationRequested) {
        clientReservationRequestsRepository.publish(anyParkingSpotReservationRequested);
        log.debug("successfully created reservation request for client with id {}", anyParkingSpotReservationRequested.getClientId());
        return Success;
    }

    private Result publishEvents(ReservationRequestFailed reservationRequestFailed) {
        clientReservationRequestsRepository.publish(reservationRequestFailed);
        log.debug("rejected to request reservation for client with id {}, reason: {}", reservationRequestFailed.getClientId(), reservationRequestFailed.getReason());
        return Rejection;
    }

    private ClientReservationRequests load(ClientId clientId) {
        return clientReservationRequestsRepository.findBy(clientId)
                .getOrElse(() -> clientReservationRequestsFactory.createEmpty(clientId));
    }

}
