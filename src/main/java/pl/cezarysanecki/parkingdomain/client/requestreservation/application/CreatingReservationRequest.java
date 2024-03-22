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
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.ReservationRequestCreated;
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.ReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;

@Slf4j
@RequiredArgsConstructor
public class CreatingReservationRequest {

    private final ClientReservationRequestsRepository clientReservationRequestsRepository;

    public Try<Result> createRequest(@NonNull CreateReservationRequestForChosenParkingSpotCommand command) {
        return Try.of(() -> {
            ClientReservationRequests clientReservationRequests = load(command.getClientId());
            Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservationRequests.reserve(
                    command.getReservationType(),
                    command.getParkingSpotId());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve parking slot", throwable));
    }

    public Try<Result> reserve(@NonNull CreateReservationRequestForAnyParkingSpotCommand command) {
        return Try.of(() -> {
            ClientReservationRequests clientReservationRequests = load(command.getClientId());
            Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservationRequests.reserve(
                    command.getReservationType(),
                    command.getParkingSpotType(),
                    command.getVehicleSizeUnit());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve parking slot", throwable));
    }

    private Result publishEvents(ReservationRequestCreated reservationRequestCreated) {
        clientReservationRequestsRepository.publish(reservationRequestCreated);
        log.debug("successfully requested reservation for client with id {}", reservationRequestCreated.getClientId());
        return Success;
    }

    private Result publishEvents(ReservationRequestFailed reservationRequestFailed) {
        clientReservationRequestsRepository.publish(reservationRequestFailed);
        log.debug("rejected to request reservation for client with id {}, reason: {}", reservationRequestFailed.getClientId(), reservationRequestFailed.getReason());
        return Rejection;
    }

    private ClientReservationRequests load(ClientId clientId) {
        return clientReservationRequestsRepository.findBy(clientId);
    }

}
