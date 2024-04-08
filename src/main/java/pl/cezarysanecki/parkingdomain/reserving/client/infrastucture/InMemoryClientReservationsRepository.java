package pl.cezarysanecki.parkingdomain.reserving.client.infrastucture;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservations;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsRepository;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
class InMemoryClientReservationsRepository implements ClientReservationsRepository {

    private static final Map<ClientId, ClientReservationsEntity> DATABASE = new ConcurrentHashMap<>();

    private final EventPublisher eventPublisher;

    @Override
    public Option<ClientReservations> findBy(ClientId clientId) {
        return Option.of(DATABASE.get(clientId))
                .map(DomainModelMapper::map);
    }

    @Override
    public Option<ClientReservations> findBy(ReservationId reservationId) {
        return Option.ofOptional(
                        DATABASE.values().stream()
                                .filter(entity -> entity.reservations.contains(reservationId.getValue()))
                                .findFirst())
                .map(DomainModelMapper::map);
    }

    @Override
    public ClientReservations publish(ClientReservationsEvent clientReservationsEvent) {
        ClientId clientId = clientReservationsEvent.getClientId();

        ClientReservationsEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsEntity(clientId.getValue(), new HashSet<>()));
        entity.handle(clientReservationsEvent);
        DATABASE.put(clientId, entity);
        eventPublisher.publish(clientReservationsEvent.normalize());
        return DomainModelMapper.map(entity);
    }

}
