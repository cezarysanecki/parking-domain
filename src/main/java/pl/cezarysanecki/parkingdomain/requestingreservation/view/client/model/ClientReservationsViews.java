package pl.cezarysanecki.parkingdomain.requestingreservation.view.client.model;

import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId;

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsEvent.ReservationForPartOfParkingSpotSubmitted;

public interface ClientReservationsViews {

    ClientReservationsView getClientReservationsViewFor(ClientId clientId);

    void handle(ReservationForPartOfParkingSpotSubmitted event);

    void handle(ReservationRequestCancelled event);

}
