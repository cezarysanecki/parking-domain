package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservations;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsEvent;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsFactory;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsRepository;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
class InMemoryClientReservationsRepository implements ClientReservationsRepository {

    private static final Map<ClientId, ClientReservationsEntity> DATABASE = new ConcurrentHashMap<>();
    private final EventPublisher eventPublisher;
    private final DomainModelMapper domainModelMapper;
    private final ClientReservationsFactory factory;

    @Override
    public ClientReservations findBy(ClientId clientId) {
        return Option.of(DATABASE.get(clientId))
                .map(domainModelMapper::map)
                .getOrElse(() -> factory.createEmpty(clientId));
    }

    @Override
    public ClientReservations publish(ClientReservationsEvent clientReservationsEvent) {
        ClientId clientId = clientReservationsEvent.getClientId();

        ClientReservationsEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsEntity(clientId.getValue()));
        entity.handle(clientReservationsEvent);
        DATABASE.put(clientId, entity);

        eventPublisher.publish(clientReservationsEvent);

        return domainModelMapper.map(entity);
    }

}
