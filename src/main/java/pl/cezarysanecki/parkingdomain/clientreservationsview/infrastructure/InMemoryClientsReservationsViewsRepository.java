package pl.cezarysanecki.parkingdomain.clientreservationsview.infrastructure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientReservationsView;
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientsReservationsViews;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class InMemoryClientsReservationsViewsRepository implements ClientsReservationsViews {

    private static final Map<ClientId, ClientReservationsViewEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public ClientReservationsView findFor(ClientId clientId) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));
        return map(entity);
    }

    @Override
    public ClientReservationsView addReservation(ClientId clientId, ParkingSpotId parkingSpotId, ReservationId reservationId, ReservationSlot reservationSlot) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));

        entity.getReservations()
                .add(new ClientReservationViewEntity(
                        reservationId.getValue(),
                        parkingSpotId.getValue(),
                        reservationSlot.getSince(),
                        reservationSlot.until()));
        DATABASE.put(clientId, entity);

        return map(entity);
    }

    @Override
    public ClientReservationsView removeReservation(ClientId clientId, ReservationId reservationId) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));

        entity.getReservations()
                .removeIf(reservation -> reservation.getReservationId().equals(reservationId.getValue()));
        DATABASE.put(clientId, entity);

        return map(entity);
    }

    private static ClientReservationsView map(ClientReservationsViewEntity entity) {
        return new ClientReservationsView(entity.getClientId(), entity.getReservations()
                .stream()
                .map(reservationEntity -> new ClientReservationsView.Reservation(
                        reservationEntity.reservationId,
                        reservationEntity.parkingSpotId,
                        reservationEntity.since,
                        reservationEntity.until))
                .collect(Collectors.toUnmodifiableSet()));
    }

}
