package pl.cezarysanecki.parkingdomain.reservation.view.client.model;

import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientId;

import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationRequestSubmitted;

public interface ClientReservationsViews {

    ClientReservationsView getClientReservationsViewFor(ClientId clientId);

    void handle(ReservationRequestSubmitted event);

    void handle(ReservationRequestCancelled event);

}
