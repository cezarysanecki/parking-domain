package pl.cezarysanecki.parkingdomain.requesting.view.client.model;

import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId;

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForPartOfParkingSpotMade;

public interface ClientRequestsViews {

    ClientRequestsView getClientRequestsViewFor(ClientId clientId);

    void handle(RequestForPartOfParkingSpotMade event);

    void handle(RequestCancelled event);

}
