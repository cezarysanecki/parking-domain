package pl.cezarysanecki.parkingdomain.reservation.client.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservations;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationForWholeParkingSpotSubmitted;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationSubmissionFailed;

@Slf4j
@RequiredArgsConstructor
public class ReservingWholeParkingSpot {

    private final ClientReservationsRepository clientReservationsRepository;

    @Value
    public static class Command {

        @NonNull ClientId clientId;
        @NonNull ParkingSpotId parkingSpotId;

    }

    public Try<Result> requestReservation(Command command) {
        ClientId clientId = command.getClientId();
        ParkingSpotId parkingSpotId = command.getParkingSpotId();

        return Try.of(() -> {
            ClientReservations clientReservations = load(clientId);
            Either<ReservationSubmissionFailed, ReservationForWholeParkingSpotSubmitted> result = clientReservations.createRequest(parkingSpotId);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(t -> log.error("Failed to place a hold", t));
    }

    private Result publishEvents(ReservationForWholeParkingSpotSubmitted requestSubmitted) {
        log.debug("reservation for whole parking spot submitted for client with id {}", requestSubmitted.getClientId());
        clientReservationsRepository.publish(requestSubmitted);
        return new Result.Success();
    }

    private Result publishEvents(ReservationSubmissionFailed requestSubmissionFailed) {
        log.debug("reservation request submission failed for client with id {}, reason: {}",
                requestSubmissionFailed.getClientId(), requestSubmissionFailed.getReason());
        clientReservationsRepository.publish(requestSubmissionFailed);
        return Result.Rejection.with(requestSubmissionFailed.getReason());
    }

    private ClientReservations load(ClientId clientId) {
        return clientReservationsRepository.findBy(clientId)
                .getOrElse(() -> ClientReservations.empty(clientId));
    }

}
