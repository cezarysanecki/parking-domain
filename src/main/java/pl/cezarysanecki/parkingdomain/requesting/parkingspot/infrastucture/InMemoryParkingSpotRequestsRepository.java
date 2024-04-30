package pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastucture;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequests;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
class InMemoryParkingSpotRequestsRepository implements ParkingSpotRequestsRepository {

    private static final Map<ParkingSpotId, ParkingSpotRequestsEntity> DATABASE = new ConcurrentHashMap<>();

    private final EventPublisher eventPublisher;

    @Override
    public ParkingSpotRequests createUsing(ParkingSpotId parkingSpotId, ParkingSpotCapacity parkingSpotCapacity) {
        ParkingSpotRequestsEntity entity = new ParkingSpotRequestsEntity(
                parkingSpotId.getValue(),
                parkingSpotCapacity.getValue(),
                new HashSet<>());
        DATABASE.put(parkingSpotId, entity);
        return DomainModelMapper.map(entity);
    }

    @Override
    public Option<ParkingSpotRequests> findBy(ParkingSpotId parkingSpotId) {
        return Option.of(DATABASE.get(parkingSpotId))
                .map(DomainModelMapper::map);
    }

    @Override
    public Option<ParkingSpotRequests> findBy(RequestId requestId) {
        return Option.ofOptional(
                        DATABASE.values()
                                .stream()
                                .filter(entity -> entity.requests.stream()
                                        .anyMatch(vehicleRequestEntity -> vehicleRequestEntity.requestId.equals(requestId.getValue())))
                                .findFirst())
                .map(DomainModelMapper::map);
    }

    @Override
    public void publish(ParkingSpotRequestEvent parkingSpotRequestEvent) {
        ParkingSpotId parkingSpotId = parkingSpotRequestEvent.getParkingSpotId();

        ParkingSpotRequestsEntity entity = DATABASE.get(parkingSpotId);
        if (entity != null) {
            entity.handle(parkingSpotRequestEvent);
            DATABASE.put(parkingSpotId, entity);
        } else {
            log.debug("cannot find parking spot requests for parking spot with id {}", parkingSpotId);
        }
        eventPublisher.publish(parkingSpotRequestEvent.normalize());
    }

}
