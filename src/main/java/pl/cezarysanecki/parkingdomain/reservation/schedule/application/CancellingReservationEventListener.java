package pl.cezarysanecki.parkingdomain.reservation.schedule.application;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationCancelled;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsRepository;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationEventListener {

    private final ParkingSpotReservationsRepository parkingSpotReservationsRepository;

    @EventListener
    public void handle(ReservationRequestCancelled event) {
        ReservationId reservationId = event.getReservationId();

        Try.of(() -> deleteBy(reservationId).map(this::publishEvents))
                .onFailure(throwable -> log.error("Failed to cancel reservation", throwable));
    }

    private Result publishEvents(ReservationId reservationId) {
        parkingSpotReservationsRepository.publish(new ReservationCancelled(reservationId));
        log.debug("successfully cancelled reservation with id {}", reservationId);
        return Success;
    }

    private Option<ReservationId> deleteBy(ReservationId reservationId) {
        return parkingSpotReservationsRepository.deleteBy(reservationId)
                .onEmpty(() -> log.error("cannot find reservation with id {}", reservationId));
    }

}
