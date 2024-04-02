package pl.cezarysanecki.parkingdomain.reservation.client.infrastucture;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservations;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsRepository;

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
        ClientReservationsEntity entity = DATABASE.get(clientReservationsEvent.getClientId());
        entity.handle(clientReservationsEvent);
        DATABASE.put(clientReservationsEvent.getClientId(), entity);
        log.debug("handled event {} for client reservations", clientReservationsEvent.getClass().getSimpleName());
        eventPublisher.publish(clientReservationsEvent.normalize());
        return DomainModelMapper.map(entity);
    }

}
