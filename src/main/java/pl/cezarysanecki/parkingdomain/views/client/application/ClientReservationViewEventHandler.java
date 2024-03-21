package pl.cezarysanecki.parkingdomain.views.client.application;

import io.vavr.API;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsEvent;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsEvent.ReservationRequestCreated;
import pl.cezarysanecki.parkingdomain.views.client.model.ClientReservationsView;
import pl.cezarysanecki.parkingdomain.views.client.model.ClientsReservationsViews;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleEvent;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleEvent.ReservationCancelled;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleEvent.ReservationMade;

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
