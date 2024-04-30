package pl.cezarysanecki.parkingdomain.requesting.client.infrastucture;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.catalogue.client.ClientId;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequests;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
class InMemoryClientRequestsRepository implements ClientRequestsRepository {

    private static final Map<ClientId, ClientRequestsEntity> DATABASE = new ConcurrentHashMap<>();

    private final EventPublisher eventPublisher;

    @Override
    public Option<ClientRequests> findBy(ClientId clientId) {
        return Option.of(DATABASE.get(clientId))
                .map(DomainModelMapper::map);
    }

    @Override
    public Option<ClientRequests> findBy(RequestId requestId) {
        return Option.ofOptional(
                        DATABASE.values().stream()
                                .filter(entity -> entity.requests.contains(requestId.getValue()))
                                .findFirst())
                .map(DomainModelMapper::map);
    }

    @Override
    public ClientRequests publish(ClientRequestsEvent clientRequestsEvent) {
        ClientId clientId = clientRequestsEvent.getClientId();

        ClientRequestsEntity entity = DATABASE.getOrDefault(clientId, new ClientRequestsEntity(clientId.getValue(), new HashSet<>()));
        entity.handle(clientRequestsEvent);
        DATABASE.put(clientId, entity);
        eventPublisher.publish(clientRequestsEvent.normalize());
        return DomainModelMapper.map(entity);
    }

}
