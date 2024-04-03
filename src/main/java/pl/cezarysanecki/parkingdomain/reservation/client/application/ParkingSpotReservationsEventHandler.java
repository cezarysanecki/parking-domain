package pl.cezarysanecki.parkingdomain.reservation.client.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsRepository;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ReservationId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.*;
import static pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationFailed;
import static pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReserved;

@Slf4j
@RequiredArgsConstructor
public class ParkingSpotReservationsEventHandler {

    private final ClientReservationsRepository clientReservationsRepository;

    @EventListener
    public void handle(ParkingSpotReservationFailed parkingSpotReservationFailed) {
        ReservationId reservationId = parkingSpotReservationFailed.getReservationId();

        clientReservationsRepository.findBy(reservationId)
                .map(clientReservations -> {
                    Either<ReservationRequestCancellationFailed, ReservationRequestCancelled> result = clientReservations.cancel(reservationId);
                })
                .onEmpty(() -> log.error("cannot find client reservations for reservation with id {}", reservationId));


        parkingSpotReservationsRepository.findBy(reservationRequest.getParkingSpotId())
                .map(parkingSpotReservations -> {
                    Either<ParkingSpotReservationFailed, ParkingSpotReserved> result = parkingSpotReservations.reserve(reservationRequest);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> log.error("cannot find reservations for parking spot"));
    }

    private Result publishEvents(ParkingSpotReservationFailed parkingSpotReservationFailed) {
        log.debug("failed to reserve parking spot with id {}, reason: {}", parkingSpotReservationFailed.getParkingSpotId(), parkingSpotReservationFailed.getReason());
        parkingSpotReservationsRepository.publish(parkingSpotReservationFailed);
        return Result.Rejection.with(parkingSpotReservationFailed.getReason());
    }

    private Result publishEvents(ParkingSpotReserved parkingSpotReserved) {
        log.debug("successfully reserved parking spot with id {}", parkingSpotReserved.getParkingSpotId());
        parkingSpotReservationsRepository.publish(parkingSpotReserved);
        return new Result.Success();
    }

}
