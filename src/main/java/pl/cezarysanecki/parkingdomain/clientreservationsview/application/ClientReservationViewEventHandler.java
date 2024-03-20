package pl.cezarysanecki.parkingdomain.clientreservationsview.application;

import io.vavr.API;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated;
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientReservationsView;
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientsReservationsViews;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@RequiredArgsConstructor
public class ClientReservationViewEventHandler {

    private final ClientsReservationsViews clientsReservationsViews;

    @EventListener
    public void handle(ClientReservationsEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationRequestCreated.class)), this::handle),
                Case($(), () -> event));
    }

    @EventListener
    public void handle(ReservationScheduleEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationMade.class)), this::handle),
                Case($(instanceOf(ReservationCancelled.class)), this::handle),
                Case($(), () -> event));
    }

    public ClientReservationsView handle(ReservationRequestCreated reservationRequestCreated) {
        ClientId clientId = reservationRequestCreated.getClientId();

        log.debug("creating reservation view for client with id {}", clientId);
        return clientsReservationsViews.addPendingReservation(
                reservationRequestCreated.getClientId(),
                reservationRequestCreated.getParkingSpotId(),
                reservationRequestCreated.getReservationId(),
                reservationRequestCreated.getReservationSlot());
    }

    public ClientReservationsView handle(ReservationMade reservationMade) {
        ClientId clientId = reservationMade.getClientId();

        log.debug("creating reservation view for client with id {}", clientId);
        return clientsReservationsViews.approveReservation(
                reservationMade.getClientId(),
                reservationMade.getParkingSpotId(),
                reservationMade.getReservationId());
    }

    private ClientReservationsView handle(ReservationCancelled reservationCancelled) {
        ClientId clientId = reservationCancelled.getClientId();

        log.debug("removing reservation view for client with id {}", clientId);
        return clientsReservationsViews.cancelReservation(
                reservationCancelled.getClientId(),
                reservationCancelled.getReservationId());
    }

}
