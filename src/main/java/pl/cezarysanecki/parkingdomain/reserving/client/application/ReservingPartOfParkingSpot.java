package pl.cezarysanecki.parkingdomain.reserving.client.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservations;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationForPartOfParkingSpotSubmitted;
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationSubmissionFailed;

@Slf4j
@RequiredArgsConstructor
public class ReservingPartOfParkingSpot {

    private final ClientReservationsRepository clientReservationsRepository;

    @Value
    public static class Command {

        @NonNull ClientId clientId;
        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleSize vehicleSize;

    }

    public Try<Result> requestReservation(Command command) {
        ClientId clientId = command.getClientId();
        ParkingSpotId parkingSpotId = command.getParkingSpotId();
        VehicleSize vehicleSize = command.getVehicleSize();

        return Try.of(() -> {
            ClientReservations clientReservations = load(clientId);
            Either<ReservationSubmissionFailed, ReservationForPartOfParkingSpotSubmitted> result = clientReservations.createRequest(parkingSpotId, vehicleSize);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(t -> log.error("Failed to place a hold", t));
    }

    private Result publishEvents(ReservationForPartOfParkingSpotSubmitted requestSubmitted) {
        log.debug("reservation for part of parking spot submitted for client with id {}", requestSubmitted.getClientId());
        clientReservationsRepository.publish(requestSubmitted);
        return new Result.Success<>(requestSubmitted.getClientId());
    }

    private Result publishEvents(ReservationSubmissionFailed requestSubmissionFailed) {
        log.debug("reservation request submission failed for client with id {}, reason: {}",
                requestSubmissionFailed.getClientId(), requestSubmissionFailed.getReason());
        clientReservationsRepository.publish(requestSubmissionFailed);
        return Result.Rejection.with(requestSubmissionFailed.getReason());
    }

    private ClientReservations load(ClientId clientId) {
        return clientReservationsRepository.findBy(clientId)
                .getOrElse(() -> {
                    log.debug("resolving new client to create reservation request");
                    return ClientReservations.empty(clientId);
                });
    }

}
