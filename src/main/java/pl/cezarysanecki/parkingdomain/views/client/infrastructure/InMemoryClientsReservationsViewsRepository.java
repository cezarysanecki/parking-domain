package pl.cezarysanecki.parkingdomain.views.client.infrastructure;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.views.client.model.ClientReservationStatus;
import pl.cezarysanecki.parkingdomain.views.client.model.ClientReservationsView;
import pl.cezarysanecki.parkingdomain.views.client.model.ClientsReservationsViews;

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
    public ClientReservationsView approveReservation(ClientId clientId, ParkingSpotId parkingSpotId, ReservationId reservationId) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));

        entity.getReservations()
                .stream()
                .filter(reservation -> reservation.reservationId.equals(reservationId.getValue()))
                .findFirst()
                .ifPresent(reservation -> {
                    reservation.parkingSpotId = parkingSpotId.getValue();
                    reservation.status = ClientReservationStatus.Approved;
                });
        DATABASE.put(clientId, entity);

        return map(entity);
    }

    @Override
    public ClientReservationsView addPendingReservation(ClientId clientId, Option<ParkingSpotId> parkingSpotId, ReservationId reservationId) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));

        entity.getReservations()
                .add(new ClientReservationViewEntity(
                        reservationId.getValue(),
                        parkingSpotId.map(ParkingSpotId::getValue).getOrNull(),
                        ClientReservationStatus.Pending));
        DATABASE.put(clientId, entity);

        return map(entity);
    }

    @Override
    public ClientReservationsView cancelReservation(ClientId clientId, ReservationId reservationId) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));

        entity.getReservations()
                .stream()
                .filter(reservation -> reservation.reservationId.equals(reservationId.getValue()))
                .findFirst()
                .ifPresent(reservation -> reservation.status = ClientReservationStatus.Cancelled);
        DATABASE.put(clientId, entity);

        return map(entity);
    }

    private static ClientReservationsView map(ClientReservationsViewEntity entity) {
        return new ClientReservationsView(entity.getClientId(), entity.getReservations()
                .stream()
                .map(reservationEntity -> new ClientReservationsView.Reservation(
                        reservationEntity.reservationId,
                        reservationEntity.parkingSpotId,
                        reservationEntity.status))
                .collect(Collectors.toUnmodifiableSet()));
    }

}
