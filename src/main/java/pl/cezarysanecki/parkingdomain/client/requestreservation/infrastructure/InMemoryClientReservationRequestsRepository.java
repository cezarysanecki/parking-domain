package pl.cezarysanecki.parkingdomain.client.requestreservation.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsFactory;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
class InMemoryClientReservationRequestsRepository implements ClientReservationRequestsRepository {

    private static final Map<ClientId, ClientReservationsEntity> DATABASE = new ConcurrentHashMap<>();
    private final EventPublisher eventPublisher;
    private final DomainModelMapper domainModelMapper;
    private final ClientReservationRequestsFactory factory;

    @Override
    public ClientReservationRequests findBy(ClientId clientId) {
        return Option.of(DATABASE.get(clientId))
                .map(domainModelMapper::map)
                .getOrElse(() -> factory.createEmpty(clientId));
    }

    @Override
    public ClientReservationRequests publish(ClientReservationRequestsEvent clientReservationRequestsEvent) {
        ClientId clientId = clientReservationRequestsEvent.getClientId();

        ClientReservationsEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationsEntity(clientId.getValue()));
        entity.handle(clientReservationRequestsEvent);
        DATABASE.put(clientId, entity);

        eventPublisher.publish(clientReservationRequestsEvent);

        return domainModelMapper.map(entity);
    }

}
