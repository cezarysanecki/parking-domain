package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationCancellationFailed;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationCancelled;

@Slf4j
@RequiredArgsConstructor
public class HandlingClientCancelledReservationEventHandler {

    private final ParkingSpotReservationsRepository parkingSpotReservationsRepository;

    @EventListener
    public void handle(ReservationRequestCancelled reservationRequestCancelled) {
        ReservationId reservationId = reservationRequestCancelled.getReservationId();

        parkingSpotReservationsRepository.findBy(reservationId)
                .map(parkingSpotReservations -> {
                    Either<ParkingSpotReservationCancellationFailed, ParkingSpotReservationCancelled> result = parkingSpotReservations.cancel(reservationId);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> log.error("cannot find reservations for parking spot to cancel reservation with id {}", reservationId));
    }

    private Result publishEvents(ParkingSpotReservationCancellationFailed parkingSpotReservationCancellationFailed) {
        log.debug("failed to cancel reservation on parking spot with id {}, reason: {}", parkingSpotReservationCancellationFailed.getParkingSpotId(), parkingSpotReservationCancellationFailed.getReason());
        parkingSpotReservationsRepository.publish(parkingSpotReservationCancellationFailed);
        return Result.Rejection.with(parkingSpotReservationCancellationFailed.getReason());
    }

    private Result publishEvents(ParkingSpotReservationCancelled parkingSpotReservationCancelled) {
        log.debug("successfully cancelled reservation on parking spot with id {}", parkingSpotReservationCancelled.getParkingSpotId());
        parkingSpotReservationsRepository.publish(parkingSpotReservationCancelled);
        return new Result.Success<>(parkingSpotReservationCancelled.getParkingSpotId());
    }

}
