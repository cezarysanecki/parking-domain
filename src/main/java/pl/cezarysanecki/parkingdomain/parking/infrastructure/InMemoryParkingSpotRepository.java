package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
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

class InMemoryParkingSpotRepository implements ParkingSpots {

    private static final Map<ParkingSpotId, ParkingSpotEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(parkingSpot -> ParkingSpotId.of(parkingSpot.parkingSpotId).equals(parkingSpotId))
                        .findFirst()
                        .map(DomainModelMapper::map));
    }

    @Override
    public Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId, Instant when) {
        return findBy(parkingSpotId);
    }

    @Override
    public ParkingSpot publish(ParkingSpotEvent event) {
        return Match(event).of(
                Case($(instanceOf(ParkingSpotCreated.class)), this::createNewParkingSpot),
                Case($(), this::handleNextEvent));
    }

    private ParkingSpot createNewParkingSpot(ParkingSpotCreated event) {
        ParkingSpotId parkingSpotId = event.getParkingSpotId();
        ParkingSpotEntity entity = new ParkingSpotEntity(parkingSpotId.getValue(), 4);
        DATABASE.put(parkingSpotId, entity);
        return DomainModelMapper.map(entity);
    }

    private ParkingSpot handleNextEvent(ParkingSpotEvent event) {
        ParkingSpotEntity entity = DATABASE.get(event.getParkingSpotId());
        entity = entity.handle(event);
        DATABASE.put(event.getParkingSpotId(), entity);
        return DomainModelMapper.map(entity);
    }

}
