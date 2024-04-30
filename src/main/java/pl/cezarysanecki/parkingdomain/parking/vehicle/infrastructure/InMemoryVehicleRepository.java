package pl.cezarysanecki.parkingdomain.parking.vehicle.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleRegistered;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

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
                domainEvent.vehicleId().getValue(),
                Option.none(),
                domainEvent.vehicleSize().getValue());
        DATABASE.put(domainEvent.vehicleId(), entity);
        return DomainModelMapper.map(entity);
    }

    private Vehicle handleNextEvent(VehicleEvent domainEvent) {
        VehicleEntity entity = DATABASE.get(domainEvent.vehicleId());
        entity.handle(domainEvent);
        return DomainModelMapper.map(entity);
    }

}
