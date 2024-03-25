package pl.cezarysanecki.parkingdomain.views.client.application;

import io.vavr.API;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.AnyParkingSpotReservationRequested;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ChosenParkingSpotReservationRequested;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationForPartOfParkingSpotMade;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationForWholeParkingSpotMade;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
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
                Case($(instanceOf(ChosenParkingSpotReservationRequested.class)), this::handle),
                Case($(instanceOf(AnyParkingSpotReservationRequested.class)), this::handle),
                Case($(instanceOf(ReservationRequestCancelled.class)), this::handle),
                Case($(), () -> event));
    }

    @EventListener
    public void handle(ParkingSpotReservationsEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationForWholeParkingSpotMade.class)), this::handle),
                Case($(instanceOf(ReservationForPartOfParkingSpotMade.class)), this::handle),
                Case($(), () -> event));
    }

    public ClientReservationsView handle(ChosenParkingSpotReservationRequested chosenParkingSpotReservationRequested) {
        ClientId clientId = chosenParkingSpotReservationRequested.getClientId();
        ParkingSpotId parkingSpotId = chosenParkingSpotReservationRequested.getParkingSpotId();
        ReservationId reservationId = chosenParkingSpotReservationRequested.getReservationId();

        log.debug("creating reservation view for client with id {}", clientId);
        return clientsReservationsViews.addPendingReservation(clientId, Option.of(parkingSpotId), reservationId);
    }

    public ClientReservationsView handle(AnyParkingSpotReservationRequested anyParkingSpotReservationRequested) {
        ClientId clientId = anyParkingSpotReservationRequested.getClientId();
        ReservationId reservationId = anyParkingSpotReservationRequested.getReservationId();

        log.debug("creating reservation view for client with id {}", clientId);
        return clientsReservationsViews.addPendingReservation(clientId, Option.none(), reservationId);
    }

    private ClientReservationsView handle(ReservationRequestCancelled reservationRequestCancelled) {
        ClientId clientId = reservationRequestCancelled.getClientId();
        ReservationId reservationId = reservationRequestCancelled.getReservationId();

        log.debug("removing reservation view for client with id {}", clientId);
        return clientsReservationsViews.cancelReservation(clientId, reservationId);
    }

    public ClientReservationsView handle(ReservationForWholeParkingSpotMade reservationForWholeParkingSpotMade) {
        ParkingSpotId parkingSpotId = reservationForWholeParkingSpotMade.getParkingSpotId();
        ReservationId reservationId = reservationForWholeParkingSpotMade.getReservationId();

        log.debug("approving reservation view with id {}", reservationId);
        return clientsReservationsViews.approveReservation(reservationId, parkingSpotId);
    }

    public ClientReservationsView handle(ReservationForPartOfParkingSpotMade reservationForPartOfParkingSpotMade) {
        ParkingSpotId parkingSpotId = reservationForPartOfParkingSpotMade.getParkingSpotId();
        ReservationId reservationId = reservationForPartOfParkingSpotMade.getReservationId();

        log.debug("approving reservation view with id {}", reservationId);
        return clientsReservationsViews.approveReservation(reservationId, parkingSpotId);
    }

}
