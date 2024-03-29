package pl.cezarysanecki.parkingdomain.client.view.application;

import io.vavr.API;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.AnyParkingSpotReservationRequested;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ChosenParkingSpotReservationRequested;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ClientReservationRequestsEvent;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.client.view.model.ClientsReservationsViews;
import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.model.events.ParkingSpotReservationsEvent;
import pl.cezarysanecki.parkingdomain.reservation.model.events.ReservationForPartOfParkingSpotMade;
import pl.cezarysanecki.parkingdomain.reservation.model.events.ReservationForWholeParkingSpotMade;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@RequiredArgsConstructor
public class ClientReservationViewEventHandler {

    private final ClientsReservationsViews clientsReservationsViews;

    @ViewEventListener
    public void handle(ClientReservationRequestsEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ChosenParkingSpotReservationRequested.class)), this::handle),
                Case($(instanceOf(AnyParkingSpotReservationRequested.class)), this::handle),
                Case($(instanceOf(ReservationRequestCancelled.class)), this::handle),
                Case($(), () -> event));
    }

    @ViewEventListener
    public void handle(ParkingSpotReservationsEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationForWholeParkingSpotMade.class)), this::handle),
                Case($(instanceOf(ReservationForPartOfParkingSpotMade.class)), this::handle),
                Case($(), () -> event));
    }

    public ClientReservationRequestsEvent handle(ChosenParkingSpotReservationRequested event) {
        ClientId clientId = event.getClientId();
        ParkingSpotId parkingSpotId = event.getParkingSpotId();
        ReservationId reservationId = event.getReservationId();

        log.debug("creating reservation view for client with id {}", clientId);
        clientsReservationsViews.addPendingReservation(clientId, Option.of(parkingSpotId), reservationId);

        return event;
    }

    public ClientReservationRequestsEvent handle(AnyParkingSpotReservationRequested event) {
        ClientId clientId = event.getClientId();
        ReservationId reservationId = event.getReservationId();

        log.debug("creating reservation view for client with id {}", clientId);
        clientsReservationsViews.addPendingReservation(clientId, Option.none(), reservationId);

        return event;
    }

    private ClientReservationRequestsEvent handle(ReservationRequestCancelled event) {
        ReservationId reservationId = event.getReservationId();

        log.debug("cancelling reservation view with id {}", reservationId);
        clientsReservationsViews.cancelReservation(reservationId);

        return event;
    }

    public ParkingSpotReservationsEvent handle(ReservationForWholeParkingSpotMade event) {
        ParkingSpotId parkingSpotId = event.getParkingSpotId();
        ReservationId reservationId = event.getReservationId();

        log.debug("approving reservation view with id {}", reservationId);
        clientsReservationsViews.approveReservation(reservationId, parkingSpotId);

        return event;
    }

    public ParkingSpotReservationsEvent handle(ReservationForPartOfParkingSpotMade event) {
        ParkingSpotId parkingSpotId = event.getParkingSpotId();
        ReservationId reservationId = event.getReservationId();

        log.debug("approving reservation view with id {}", reservationId);
        clientsReservationsViews.approveReservation(reservationId, parkingSpotId);

        return event;
    }

}
