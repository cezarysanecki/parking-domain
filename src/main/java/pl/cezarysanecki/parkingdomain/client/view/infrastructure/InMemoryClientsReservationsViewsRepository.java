package pl.cezarysanecki.parkingdomain.client.view.infrastructure;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.view.model.ClientReservationStatus;
import pl.cezarysanecki.parkingdomain.client.view.model.ClientReservationsView;
import pl.cezarysanecki.parkingdomain.client.view.model.ClientsReservationsViews;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class InMemoryClientsReservationsViewsRepository implements ClientsReservationsViews {

    private static final Map<ClientId, ClientReservationsViewEntity> DATABASE = new ConcurrentHashMap<>();
    private static final Map<ReservationId, ParkingSpotId> DATABASE_APPROVED_RESERVATIONS = new ConcurrentHashMap<>();

    @Override
    public ClientReservationsView findFor(ClientId clientId) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));
        return ViewModelMapper.map(entity);
    }

    @Override
    public void approveReservation(ReservationId reservationId, ParkingSpotId parkingSpotId) {
        ClientReservationsViewEntity entity = DATABASE.values().stream()
                .filter(clientReservationsViewEntity -> clientReservationsViewEntity.reservations.stream()
                        .anyMatch(clientReservationViewEntity -> clientReservationViewEntity.getReservationId().equals(reservationId.getValue())))
                .findFirst()
                .orElse(null);
        if (entity == null) {
            DATABASE_APPROVED_RESERVATIONS.put(reservationId, parkingSpotId);
            log.debug("cannot find pending reservation request with id {} - need to store this info in temp", reservationId);
            return;
        }

        entity.getReservations()
                .stream()
                .filter(reservation -> reservation.reservationId.equals(reservationId.getValue()))
                .findFirst()
                .ifPresent(reservation -> {
                    reservation.parkingSpotId = parkingSpotId.getValue();
                    reservation.status = ClientReservationStatus.Approved;
                });
        DATABASE.put(ClientId.of(entity.clientId), entity);
    }

    @Override
    public void addPendingReservation(ClientId clientId, Option<ParkingSpotId> parkingSpotId, ReservationId reservationId) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));

        ParkingSpotId approvedParkingSpotId = DATABASE_APPROVED_RESERVATIONS.remove(reservationId);
        ParkingSpotId parkingSpotToSave = approvedParkingSpotId != null ? approvedParkingSpotId : parkingSpotId.getOrNull();

        if (parkingSpotToSave == null) {
            log.error("there is no parking spot to assign to client - look at that");
        }

        entity.getReservations()
                .add(new ClientReservationViewEntity(
                        reservationId.getValue(),
                        Option.of(parkingSpotToSave).map(ParkingSpotId::getValue).getOrNull(),
                        approvedParkingSpotId != null ? ClientReservationStatus.Approved : ClientReservationStatus.Pending));
        DATABASE.put(clientId, entity);
    }

    @Override
    public void cancelReservation(ClientId clientId, ReservationId reservationId) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));

        entity.getReservations()
                .stream()
                .filter(reservation -> reservation.reservationId.equals(reservationId.getValue()))
                .findFirst()
                .ifPresent(reservation -> reservation.status = ClientReservationStatus.Cancelled);
        DATABASE.put(clientId, entity);
    }

    @Override
    public void rejectReservation(ClientId clientId, ReservationId reservationId) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));

        entity.getReservations()
                .stream()
                .filter(reservation -> reservation.reservationId.equals(reservationId.getValue()))
                .findFirst()
                .ifPresent(reservation -> reservation.status = ClientReservationStatus.Rejected);
        DATABASE.put(clientId, entity);
    }

}
