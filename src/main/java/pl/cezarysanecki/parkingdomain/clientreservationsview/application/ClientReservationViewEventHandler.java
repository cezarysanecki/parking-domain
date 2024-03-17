package pl.cezarysanecki.parkingdomain.clientreservationsview.application;

import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
public class ClientReservationViewEventHandler {

    @EventListener
    public void handle(ParkingSpotEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ParkingSpotEvent.ReservationFulfilled.class)), this::handle),
                Case($(), () -> event));
    }

    @EventListener
    public void handle(ReservationScheduleEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationScheduleEvent.ReservationMade.class)), this::handle),
                Case($(instanceOf(ReservationScheduleEvent.ReservationCancelled.class)), this::handle),
                Case($(), () -> event));
    }

}
