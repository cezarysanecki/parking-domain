package pl.cezarysanecki.parkingdomain.parking.view.vehicle.infrastructure;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.RegisteringVehicle.VehicleRegistered;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleDroveAway;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.model.VehicleView;
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.model.VehicleViews;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleParked;

@Slf4j
class InMemoryVehicleViewRepository implements VehicleViews {

    private static final Map<VehicleId, VehicleViewEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public Set<VehicleView> queryForNotParkedVehicles() {
        return DATABASE.values().stream()
                .filter(entity -> entity.parkingSpotId.isEmpty())
                .map(entity -> new VehicleView(
                        entity.vehicleId,
                        entity.parkingSpotId.getOrNull()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<VehicleView> queryForParkedVehicles() {
        return DATABASE.values().stream()
                .filter(entity -> entity.parkingSpotId.isDefined())
                .map(entity -> new VehicleView(
                        entity.vehicleId,
                        entity.parkingSpotId.getOrNull()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<VehicleView> queryForAllVehicles() {
        return DATABASE.values().stream()
                .map(entity -> new VehicleView(
                        entity.vehicleId,
                        entity.parkingSpotId.getOrNull()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @ViewEventListener
    public void handle(VehicleRegistered event) {
        DATABASE.put(event.getVehicleId(), new VehicleViewEntity(event.getVehicleId().getValue(), Option.none()));
    }

    @Override
    @ViewEventListener
    public void handle(VehicleParked event) {
        Option.of(DATABASE.get(event.getVehicleId()))
                .map(entity -> {
                    entity.parkingSpotId = Option.of(event.getParkingSpotId().getValue());
                    return entity;
                });
    }

    @Override
    @ViewEventListener
    public void handle(VehicleDroveAway event) {
        Option.of(DATABASE.get(event.getVehicleId()))
                .map(entity -> {
                    entity.parkingSpotId = Option.none();
                    return entity;
                });
    }

}
