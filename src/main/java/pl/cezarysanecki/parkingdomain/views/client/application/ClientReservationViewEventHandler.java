package pl.cezarysanecki.parkingdomain.views.client.application;

import io.vavr.API;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestCreated;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationCancelled;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationMade;
import pl.cezarysanecki.parkingdomain.views.client.model.ClientReservationsView;
import pl.cezarysanecki.parkingdomain.views.client.model.ClientsReservationsViews;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@RequiredArgsConstructor
public class ClientReservationViewEventHandler {

    private final ClientsReservationsViews clientsReservationsViews;

    @EventListener
    public void handle(ClientReservationRequestsEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationRequestCreated.class)), this::handle),
                Case($(), () -> event));
    }

    @EventListener
    public void handle(ParkingSpotReservationsEvent event) {
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
                reservationRequestCreated.getReservationId());
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
