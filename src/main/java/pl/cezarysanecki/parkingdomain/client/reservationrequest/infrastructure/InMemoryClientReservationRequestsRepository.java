package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
class InMemoryClientReservationRequestsRepository implements ClientReservationRequestsRepository {

    private static final Map<ClientId, ClientReservationsEntity> DATABASE = new ConcurrentHashMap<>();
    private final EventPublisher eventPublisher;

    @Override
    public Option<ClientReservationRequests> findBy(ClientId clientId) {
        return Option.of(DATABASE.get(clientId))
                .map(DomainModelMapper::map);
    }

    @Override
    public Option<ClientReservationRequests> findBy(ReservationId reservationId) {
        return Option.ofOptional(
                DATABASE.values().stream()
                        .filter(entity -> entity.clientReservations.stream()
                                .anyMatch(reservation -> reservation.equals(reservationId.getValue())))
                        .findFirst()
                        .map(DomainModelMapper::map));
    }

    @Override
    public ClientReservationRequests publish(ClientReservationRequestsEvent clientReservationRequestsEvent) {
        ClientId clientId = clientReservationRequestsEvent.getClientId();

        ClientReservationsEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsEntity(clientId.getValue()));
        entity.handle(clientReservationRequestsEvent);
        DATABASE.put(clientId, entity);

        eventPublisher.publish(clientReservationRequestsEvent);

        return DomainModelMapper.map(entity);
    }

}
