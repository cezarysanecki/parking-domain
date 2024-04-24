package pl.cezarysanecki.parkingdomain.requesting.view.client.infrastructure;

import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.requesting.view.client.model.ClientReservationRequestsView;
import pl.cezarysanecki.parkingdomain.requesting.view.client.model.ClientReservationRequestsViews;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForPartOfParkingSpotMade;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled;

class InMemoryClientReservationRequestsViewRepository implements ClientReservationRequestsViews {

    private static final Map<ClientId, ClientReservationRequestsViewEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public ClientReservationRequestsView getClientReservationRequestsViewFor(ClientId clientId) {
        ClientReservationRequestsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationRequestsViewEntity(clientId.getValue(), new HashSet<>()));
        return new ClientReservationRequestsView(entity.clientId, entity.currentReservationRequests);
    }

    @Override
    @ViewEventListener
    public void handle(RequestForPartOfParkingSpotMade event) {
        ClientId clientId = event.getClientId();

        ClientReservationRequestsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationRequestsViewEntity(clientId.getValue(), new HashSet<>()));
        entity.currentReservationRequests.add(event.getReservationId().getValue());
        DATABASE.put(clientId, entity);
    }

    @Override
    @ViewEventListener
    public void handle(RequestCancelled event) {
        ClientId clientId = event.getClientId();

        ClientReservationRequestsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationRequestsViewEntity(clientId.getValue(), new HashSet<>()));
        entity.currentReservationRequests.removeIf(reservationRequest -> reservationRequest.equals(event.getReservationId().getValue()));

        DATABASE.put(clientId, entity);
    }

}
