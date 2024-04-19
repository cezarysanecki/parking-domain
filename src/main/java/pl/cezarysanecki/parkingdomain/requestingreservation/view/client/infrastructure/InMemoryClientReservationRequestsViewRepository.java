package pl.cezarysanecki.parkingdomain.requestingreservation.view.client.infrastructure;

import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.client.model.ClientReservationRequestsView;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.client.model.ClientReservationRequestsViews;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForPartOfParkingSpotRequested;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationRequestCancelled;

class InMemoryClientReservationRequestsViewRepository implements ClientReservationRequestsViews {

    private static final Map<ClientId, ClientReservationRequestsViewEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public ClientReservationRequestsView getClientReservationRequestsViewFor(ClientId clientId) {
        ClientReservationRequestsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationRequestsViewEntity(clientId.getValue(), new HashSet<>()));
        return new ClientReservationRequestsView(entity.clientId, entity.currentReservationRequests);
    }

    @Override
    @ViewEventListener
    public void handle(ReservationForPartOfParkingSpotRequested event) {
        ClientId clientId = event.getClientId();

        ClientReservationRequestsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationRequestsViewEntity(clientId.getValue(), new HashSet<>()));
        entity.currentReservationRequests.add(event.getReservationId().getValue());
        DATABASE.put(clientId, entity);
    }

    @Override
    @ViewEventListener
    public void handle(ReservationRequestCancelled event) {
        ClientId clientId = event.getClientId();

        ClientReservationRequestsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationRequestsViewEntity(clientId.getValue(), new HashSet<>()));
        entity.currentReservationRequests.removeIf(reservationRequest -> reservationRequest.equals(event.getReservationId().getValue()));

        DATABASE.put(clientId, entity);
    }

}
