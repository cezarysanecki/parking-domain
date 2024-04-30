package pl.cezarysanecki.parkingdomain.requesting.view.client.infrastructure;

import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.catalogue.client.ClientId;
import pl.cezarysanecki.parkingdomain.requesting.view.client.model.ClientRequestsView;
import pl.cezarysanecki.parkingdomain.requesting.view.client.model.ClientRequestsViews;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForPartOfParkingSpotMade;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForWholeParkingSpotMade;

class InMemoryClientRequestsViewRepository implements ClientRequestsViews {

    private static final Map<ClientId, ClientRequestsViewEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public ClientRequestsView getClientRequestsViewFor(ClientId clientId) {
        ClientRequestsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientRequestsViewEntity(clientId.getValue(), new HashSet<>()));
        return new ClientRequestsView(entity.clientId, entity.currentRequests);
    }

    @Override
    @ViewEventListener
    public void handle(RequestForPartOfParkingSpotMade event) {
        ClientId clientId = event.getClientId();

        ClientRequestsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientRequestsViewEntity(clientId.getValue(), new HashSet<>()));
        entity.currentRequests.add(event.getRequestId().getValue());
        DATABASE.put(clientId, entity);
    }

    @Override
    @ViewEventListener
    public void handle(RequestForWholeParkingSpotMade event) {
        ClientId clientId = event.getClientId();

        ClientRequestsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientRequestsViewEntity(clientId.getValue(), new HashSet<>()));
        entity.currentRequests.add(event.getRequestId().getValue());
        DATABASE.put(clientId, entity);
    }

    @Override
    @ViewEventListener
    public void handle(RequestCancelled event) {
        ClientId clientId = event.getClientId();

        ClientRequestsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientRequestsViewEntity(clientId.getValue(), new HashSet<>()));
        entity.currentRequests.removeIf(request -> request.equals(event.getRequestId().getValue()));

        DATABASE.put(clientId, entity);
    }

}
