package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservations;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsRepository;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsEvent.ReservationRequestCreated;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsEvent.ReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;

@Slf4j
@RequiredArgsConstructor
public class RequestingReservation {

    private final ClientReservationsRepository clientReservationsRepository;

    public Try<Result> createReservationRequest(@NonNull ReserveAnyParkingSpotCommand command) {
        return Try.of(() -> {
            ClientReservations clientReservations = load(command.getClientId());
            Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.requestReservation(command.getReservationSlot());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve parking slot", throwable));
    }

    public Try<Result> createReservationRequest(@NonNull ReserveChosenParkingSpotCommand command) {
        return Try.of(() -> {
            ClientReservations clientReservations = load(command.getClientId());
            Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.requestReservation(
                    command.getParkingSpotId(), command.getReservationSlot());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve parking slot", throwable));
    }

    private Result publishEvents(ReservationRequestCreated reservationRequestCreated) {
        clientReservationsRepository.publish(reservationRequestCreated);
        log.debug("successfully requested reservation for client with id {}", reservationRequestCreated.getClientId());
        return Success;
    }

    private Result publishEvents(ReservationRequestFailed reservationRequestFailed) {
        clientReservationsRepository.publish(reservationRequestFailed);
        log.debug("rejected to request reservation for client with id {}, reason: {}", reservationRequestFailed.getClientId(), reservationRequestFailed.getReason());
        return Rejection;
    }

    private ClientReservations load(ClientId clientId) {
        return clientReservationsRepository.findBy(clientId);
    }

}
