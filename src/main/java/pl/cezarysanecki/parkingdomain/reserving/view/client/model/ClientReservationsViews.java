package pl.cezarysanecki.parkingdomain.reserving.view.client.model;

import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientId;

import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationForPartOfParkingSpotSubmitted;

public interface ClientReservationsViews {

    ClientReservationsView getClientReservationsViewFor(ClientId clientId);

    void handle(ReservationForPartOfParkingSpotSubmitted event);

    void handle(ReservationRequestCancelled event);

}
