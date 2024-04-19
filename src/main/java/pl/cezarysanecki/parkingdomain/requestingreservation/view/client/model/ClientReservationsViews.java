package pl.cezarysanecki.parkingdomain.requestingreservation.view.client.model;

import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId;

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForPartOfParkingSpotRequested;

public interface ClientReservationsViews {

    ClientReservationsView getClientReservationsViewFor(ClientId clientId);

    void handle(ReservationForPartOfParkingSpotRequested event);

    void handle(ReservationRequestCancelled event);

}
