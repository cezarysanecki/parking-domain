package pl.cezarysanecki.parkingdomain.parkingspot.parking.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.CreatingParkingSpot.ParkingSpotCreated;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.OccupiedParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.OpenParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.vehicle.model.VehicleId;

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
    public Option<OpenParkingSpot> findOpenBy(ParkingSpotId parkingSpotId) {
        return Option.of(DATABASE.get(parkingSpotId))
                .map(DomainModelMapper::mapOpen);
    }

    @Override
    public Option<OccupiedParkingSpot> findOccupiedBy(VehicleId vehicleId) {
        return Option.ofOptional(
                        DATABASE.values().stream()
                                .filter(entity -> entity.vehicles.stream()
                                        .anyMatch(vehicle -> vehicle.vehicleId.equals(vehicleId.getValue())))
                                .findFirst())
                .map(DomainModelMapper::mapOccupied);
    }

    @Override
    public ParkingSpot publish(ParkingSpotEvent domainEvent) {
        ParkingSpot result = Match(domainEvent).of(
                Case($(instanceOf(ParkingSpotCreated.class)), this::createNewParkingSpot),
                Case($(), this::handleNextEvent));
        eventPublisher.publish(domainEvent.normalize());
        return result;
    }

    private ParkingSpot createNewParkingSpot(ParkingSpotCreated domainEvent) {
        ParkingSpotEntity entity = DATABASE.put(domainEvent.getParkingSpotId(), new ParkingSpotEntity(
                domainEvent.getParkingSpotId().getValue(),
                domainEvent.getParkingSpotCapacity().getValue()));
        return DomainModelMapper.mapOpen(entity);
    }

    private OpenParkingSpot handleNextEvent(ParkingSpotEvent domainEvent) {
        log.debug("handling parking spot event {}", domainEvent.getClass().getSimpleName());
        ParkingSpotEntity entity = DATABASE.get(domainEvent.getParkingSpotId());
        entity.handle(domainEvent);
        return DomainModelMapper.mapOpen(entity);
    }

}
