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

    @Override
    public ClientReservationsView findFor(ClientId clientId) {
        ClientReservationsViewEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsViewEntity(clientId.getValue(), new HashSet<>()));
        return ViewModelMapper.map(entity);
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

        return ViewModelMapper.map(entity);
    }

    @Override
    public Option<ClientReservationsView> approveReservation(ReservationId reservationId, ParkingSpotId parkingSpotId) {
        return findBy(reservationId)
                .map(entity -> {
                    entity.getReservations()
                            .stream()
                            .filter(reservation -> reservation.reservationId.equals(reservationId.getValue()))
                            .findFirst()
                            .ifPresent(reservation -> {
                                reservation.status = ClientReservationStatus.Approved;
                                reservation.parkingSpotId = parkingSpotId.getValue();
                            });
                    return DATABASE.put(ClientId.of(entity.getClientId()), entity);
                })
                .map(ViewModelMapper::map);
    }

    @Override
    public Option<ClientReservationsView> cancelReservation(ReservationId reservationId) {
        return findBy(reservationId)
                .map(entity -> {
                    entity.getReservations()
                            .stream()
                            .filter(reservation -> reservation.reservationId.equals(reservationId.getValue()))
                            .findFirst()
                            .ifPresent(reservation -> reservation.status = ClientReservationStatus.Cancelled);
                    return DATABASE.put(ClientId.of(entity.getClientId()), entity);
                })
                .map(ViewModelMapper::map);
    }

    private Option<ClientReservationsViewEntity> findBy(ReservationId reservationId) {
        return Option.ofOptional(
                        DATABASE.values()
                                .stream()
                                .filter(entity -> entity.reservations.stream()
                                        .anyMatch(reservationEntity -> reservationEntity.getReservationId().equals(reservationId.getValue())))
                                .findFirst())
                .onEmpty(() -> log.debug("cannot find client view containing reservation with id {}", reservationId));
    }

}
