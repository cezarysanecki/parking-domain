package pl.cezarysanecki.parkingdomain.reservation.application;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservations;
import pl.cezarysanecki.parkingdomain.reservation.model.events.ReservationCancelled;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationEventListener {

    private final ParkingSpotReservationsRepository parkingSpotReservationsRepository;

    @EventListener
    public void handle(ReservationRequestCancelled event) {
        ReservationId reservationId = event.getReservationId();

        Try.of(() -> load(reservationId).map(parkingSpotReservations -> publishEvents(parkingSpotReservations, reservationId)))
                .onFailure(throwable -> log.error("Failed to cancel reservation", throwable));
    }

    private Result publishEvents(ParkingSpotReservations parkingSpotReservations, ReservationId reservationId) {
        parkingSpotReservationsRepository.publish(new ReservationCancelled(reservationId, parkingSpotReservations.getParkingSpotId()));
        log.debug("successfully cancelled reservation with id {}", reservationId);
        return new Success();
    }

    private Option<ParkingSpotReservations> load(ReservationId reservationId) {
        return parkingSpotReservationsRepository.findBy(reservationId)
                .onEmpty(() -> log.error("cannot find reservations with reservation id {}", reservationId));
    }

}
