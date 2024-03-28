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
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent.ReservationForPartOfParkingSpotMade;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent.ReservationForWholeParkingSpotMade;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
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

    public ClientReservationRequestsEvent handle(ChosenParkingSpotReservationRequested chosenParkingSpotReservationRequested) {
        ClientId clientId = chosenParkingSpotReservationRequested.getClientId();
        ParkingSpotId parkingSpotId = chosenParkingSpotReservationRequested.getParkingSpotId();
        ReservationId reservationId = chosenParkingSpotReservationRequested.getReservationId();

        log.debug("creating reservation view for client with id {}", clientId);
        clientsReservationsViews.addPendingReservation(clientId, Option.of(parkingSpotId), reservationId);

        return chosenParkingSpotReservationRequested;
    }

    public ClientReservationRequestsEvent handle(AnyParkingSpotReservationRequested anyParkingSpotReservationRequested) {
        ClientId clientId = anyParkingSpotReservationRequested.getClientId();
        ReservationId reservationId = anyParkingSpotReservationRequested.getReservationId();

        log.debug("creating reservation view for client with id {}", clientId);
        clientsReservationsViews.addPendingReservation(clientId, Option.none(), reservationId);

        return anyParkingSpotReservationRequested;
    }

    private ClientReservationRequestsEvent handle(ReservationRequestCancelled reservationRequestCancelled) {
        ClientId clientId = reservationRequestCancelled.getClientId();
        ReservationId reservationId = reservationRequestCancelled.getReservationId();

        log.debug("removing reservation view for client with id {}", clientId);
        clientsReservationsViews.cancelReservation(clientId, reservationId);

        return reservationRequestCancelled;
    }

    public ParkingSpotReservationsEvent handle(ReservationForWholeParkingSpotMade reservationForWholeParkingSpotMade) {
        ParkingSpotId parkingSpotId = reservationForWholeParkingSpotMade.getParkingSpotId();
        ReservationId reservationId = reservationForWholeParkingSpotMade.getReservationId();

        log.debug("approving reservation view with id {}", reservationId);
        clientsReservationsViews.approveReservation(reservationId, parkingSpotId);

        return reservationForWholeParkingSpotMade;
    }

    public ParkingSpotReservationsEvent handle(ReservationForPartOfParkingSpotMade reservationForPartOfParkingSpotMade) {
        ParkingSpotId parkingSpotId = reservationForPartOfParkingSpotMade.getParkingSpotId();
        ReservationId reservationId = reservationForPartOfParkingSpotMade.getReservationId();

        log.debug("approving reservation view with id {}", reservationId);
        clientsReservationsViews.approveReservation(reservationId, parkingSpotId);

        return reservationForPartOfParkingSpotMade;
    }

}
