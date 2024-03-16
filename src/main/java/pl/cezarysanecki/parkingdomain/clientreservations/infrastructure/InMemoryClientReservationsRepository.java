package pl.cezarysanecki.parkingdomain.clientreservations.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservations;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsRepository;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
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
    public ClientReservations publish(ClientReservationsEvent clientReservationsEvent) {
        ClientId clientId = clientReservationsEvent.getClientId();

        ClientReservationsEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsEntity(clientId.getValue()));
        entity.handle(clientReservationsEvent);
        DATABASE.put(clientId, entity);

        eventPublisher.publish(clientReservationsEvent);

        return DomainModelMapper.map(entity);
    }

}
