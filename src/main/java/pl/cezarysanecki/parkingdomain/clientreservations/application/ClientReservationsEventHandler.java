package pl.cezarysanecki.parkingdomain.clientreservations.application;

import io.vavr.API;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservations;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsRepository;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled;
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationFailed;

@Slf4j
@RequiredArgsConstructor
public class ClientReservationsEventHandler {

    private final ClientReservationsRepository clientReservationsRepository;

    @EventListener
    public void handle(ReservationScheduleEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationMade.class)), this::handle),
                Case($(instanceOf(ReservationCancelled.class)), this::handle),
                Case($(instanceOf(ReservationFailed.class)), this::handle),
                Case($(), () -> event));
    }

    private ReservationScheduleEvent handle(ReservationMade reservationMade) {
        ClientReservations clientReservations = clientReservationsRepository.findBy(reservationMade.getClientId());
        clientReservations.approve(reservationMade.getReservationId())
                .map(clientReservationsRepository::publish)
                .peek(reservations -> log.debug("reservation with it {} has been approved", reservationMade.getReservationId()));
        return reservationMade;
    }

    private ReservationScheduleEvent handle(ReservationCancelled reservationCancelled) {
        ClientReservations clientReservations = clientReservationsRepository.findBy(reservationCancelled.getClientId());
        clientReservations.cancel(reservationCancelled.getReservationId())
                .map(clientReservationsRepository::publish)
                .peek(reservations -> log.debug("reservation with it {} has been cancelled", reservationCancelled.getReservationId()));
        return reservationCancelled;
    }

    private ReservationScheduleEvent handle(ReservationFailed reservationFailed) {
        ClientReservations clientReservations = clientReservationsRepository.findBy(reservationFailed.getClientId());
        clientReservations.cancel(reservationFailed.getReservationId())
                .map(clientReservationsRepository::publish)
                .peek(reservations -> log.debug("reservation with it {} has been cancelled", reservationFailed.getReservationId()));
        return reservationFailed;
    }

}
