package pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastucture;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
class InMemoryClientReservationRequestsRepository implements ClientReservationRequestsRepository {

    private static final Map<ClientId, ClientReservationRequestsEntity> DATABASE = new ConcurrentHashMap<>();

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
                                .filter(entity -> entity.reservations.contains(reservationId.getValue()))
                                .findFirst())
                .map(DomainModelMapper::map);
    }

    @Override
    public ClientReservationRequests publish(ClientReservationRequestsEvent clientReservationRequestsEvent) {
        ClientId clientId = clientReservationRequestsEvent.getClientId();

        ClientReservationRequestsEntity entity = DATABASE.getOrDefault(clientId, new ClientReservationRequestsEntity(clientId.getValue(), new HashSet<>()));
        entity.handle(clientReservationRequestsEvent);
        DATABASE.put(clientId, entity);
        eventPublisher.publish(clientReservationRequestsEvent.normalize());
        return DomainModelMapper.map(entity);
    }

}
