package pl.cezarysanecki.parkingdomain.requesting.view.client.model;

import pl.cezarysanecki.parkingdomain.catalogue.client.ClientId;

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForPartOfParkingSpotMade;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForWholeParkingSpotMade;

public interface ClientRequestsViews {

    ClientRequestsView getClientRequestsViewFor(ClientId clientId);

    void handle(RequestForPartOfParkingSpotMade event);

    void handle(RequestForWholeParkingSpotMade event);

    void handle(RequestCancelled event);

}
