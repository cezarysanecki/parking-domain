package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent;

@RequiredArgsConstructor
public class ParkingEventHandler {



    @EventListener
    public void handle(ReservationScheduleEvent.ReservationMade reservationMade) {
        operateOn(reservations -> {
            reservations.add(reservationMade);
            return true;
        });
    }

    @EventListener
    public void handle(ReservationScheduleEvent.ReservationCancelled reservationCancelled) {
        operateOn(reservations -> {
            reservations.removeIf(reservation -> reservation.getReservationId().equals(reservationCancelled.getReservationId()));
            return true;
        });
    }

}
