package pl.cezarysanecki.parkingdomain.requestingreservation.view.client.infrastructure;

import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.client.model.ClientReservationsView;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.client.model.ClientReservationsViews;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForPartOfParkingSpotRequested;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationRequestCancelled;

class InMemoryClientReservationsViewRepository implements ClientReservationsViews {

    private static final Map<ClientId, ClientReservationsViewEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public ClientReservationsView getClientReservationsViewFor(ClientId clientId) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));
        return new ClientReservationsView(entity.clientId, entity.currentReservations);
    }

    @Override
    @ViewEventListener
    public void handle(ReservationForPartOfParkingSpotRequested event) {
        ClientId clientId = event.getClientId();

        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));
        entity.currentReservations.add(event.getReservationId().getValue());
        DATABASE.put(clientId, entity);
    }

    @Override
    @ViewEventListener
    public void handle(ReservationRequestCancelled event) {
        ClientId clientId = event.getClientId();

        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));
        entity.currentReservations.removeIf(reservation -> reservation.equals(event.getReservationId().getValue()));

        DATABASE.put(clientId, entity);
    }

}
