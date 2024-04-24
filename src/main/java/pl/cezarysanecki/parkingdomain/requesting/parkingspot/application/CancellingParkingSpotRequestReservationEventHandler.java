package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancelled;

@Slf4j
@RequiredArgsConstructor
public class CancellingParkingSpotRequestReservationEventHandler {

    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;

    @EventListener
    public void handle(RequestCancelled requestCancelled) {
        ReservationId reservationId = requestCancelled.getReservationId();

        parkingSpotReservationRequestsRepository.findBy(reservationId)
                .map(parkingSpotReservations -> {
                    Either<ParkingSpotReservationRequestCancellationFailed, ParkingSpotReservationRequestCancelled> result = parkingSpotReservations.cancel(reservationId);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> log.error("cannot find reservations for parking spot to cancel reservation with id {}", reservationId));
    }

    private Result publishEvents(ParkingSpotReservationRequestCancellationFailed parkingSpotReservationCancellationFailed) {
        log.debug("failed to cancel reservation on parking spot with id {}, reason: {}", parkingSpotReservationCancellationFailed.getParkingSpotId(), parkingSpotReservationCancellationFailed.getReason());
        parkingSpotReservationRequestsRepository.publish(parkingSpotReservationCancellationFailed);
        return Result.Rejection.with(parkingSpotReservationCancellationFailed.getReason());
    }

    private Result publishEvents(ParkingSpotReservationRequestCancelled parkingSpotReservationCancelled) {
        log.debug("successfully cancelled reservation on parking spot with id {}", parkingSpotReservationCancelled.getParkingSpotId());
        parkingSpotReservationRequestsRepository.publish(parkingSpotReservationCancelled);
        return new Result.Success<>(parkingSpotReservationCancelled.getParkingSpotId());
    }

}
