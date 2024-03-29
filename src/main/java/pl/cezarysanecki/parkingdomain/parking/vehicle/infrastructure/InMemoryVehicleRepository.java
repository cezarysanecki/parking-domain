package pl.cezarysanecki.parkingdomain.parking.vehicle.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.RegisteringVehicle.VehicleRegistered;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@RequiredArgsConstructor
class InMemoryVehicleRepository implements Vehicles {

    private static final Map<VehicleId, VehicleEntity> DATABASE = new ConcurrentHashMap<>();

    private final EventPublisher eventPublisher;

    @Override
    public Option<Vehicle> findBy(VehicleId vehicleId) {
        return Option.of(DATABASE.get(vehicleId))
                .map(DomainModelMapper::map);
    }

    @Override
    public Vehicle publish(VehicleEvent domainEvent) {
        Vehicle result = Match(domainEvent).of(
                Case($(instanceOf(VehicleRegistered.class)), this::createNewVehicle),
                Case($(), this::handleNextEvent));
        eventPublisher.publish(domainEvent.normalize());
        return result;
    }

    private Vehicle createNewVehicle(VehicleRegistered domainEvent) {
        VehicleEntity entity = new VehicleEntity(
                domainEvent.getVehicleId().getValue(),
                Option.none(),
                domainEvent.getVehicleSize().getValue());
        DATABASE.put(domainEvent.getVehicleId(), entity);
        log.debug("creating vehicle with id {}", domainEvent.getVehicleId());
        return DomainModelMapper.map(entity);
    }

    private Vehicle handleNextEvent(VehicleEvent domainEvent) {
        VehicleEntity entity = DATABASE.get(domainEvent.getVehicleId());
        entity.handle(domainEvent);
        return DomainModelMapper.map(entity);
    }

}
