package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.CommonParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@RequiredArgsConstructor
class InMemoryParkingSpotRepository implements ParkingSpots {

    private static final Map<ParkingSpotId, ParkingSpotEntity> DATABASE = new ConcurrentHashMap<>();
    private final EventPublisher eventPublisher;

    @Override
    public Option<CommonParkingSpot> findBy(ParkingSpotId parkingSpotId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(parkingSpot -> ParkingSpotId.of(parkingSpot.parkingSpotId).equals(parkingSpotId))
                        .findFirst()
                        .map(DomainModelMapper::map));
    }

    @Override
    public Option<CommonParkingSpot> findBy(ParkingSpotId parkingSpotId, Instant when) {
        return findBy(parkingSpotId);
    }

    @Override
    public CommonParkingSpot publish(ParkingSpotEvent event) {
        CommonParkingSpot result = Match(event).of(
                Case($(instanceOf(ParkingSpotCreated.class)), this::createNewParkingSpot),
                Case($(), this::handleNextEvent));
        eventPublisher.publish(event.normalize());
        return result;
    }

    private CommonParkingSpot createNewParkingSpot(ParkingSpotCreated event) {
        ParkingSpotId parkingSpotId = event.getParkingSpotId();
        ParkingSpotEntity entity = new ParkingSpotEntity(parkingSpotId.getValue(), event.getCapacity());
        DATABASE.put(parkingSpotId, entity);
        log.debug("creating parking spot with id {}", parkingSpotId);
        return DomainModelMapper.map(entity);
    }

    private CommonParkingSpot handleNextEvent(ParkingSpotEvent event) {
        ParkingSpotEntity entity = DATABASE.get(event.getParkingSpotId());
        entity = entity.handle(event);
        DATABASE.put(event.getParkingSpotId(), entity);
        return DomainModelMapper.map(entity);
    }

}
