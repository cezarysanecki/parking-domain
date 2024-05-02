package pl.cezarysanecki.parkingdomain.parking.view.parkingspot.infrastructure;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.view.parkingspot.infrastructure.ParkingSpotViewEntity.ParkedVehicleView;
import pl.cezarysanecki.parkingdomain.parking.view.parkingspot.model.ParkingSpotView;
import pl.cezarysanecki.parkingdomain.parking.view.parkingspot.model.ParkingSpotViews;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.Released;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.Occupied;

@Slf4j
class InMemoryParkingSpotViewRepository implements ParkingSpotViews {

    private static final Map<ParkingSpotId, ParkingSpotViewEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public Set<ParkingSpotView> queryForAvailableParkingSpots() {
        return DATABASE.values().stream()
                .map(entity -> new ParkingSpotView(
                        entity.parkingSpotId,
                        entity.parkedVehicles.stream()
                                .map(vehicle -> new ParkingSpotView.ParkedVehicleView(vehicle.vehicleId))
                                .collect(Collectors.toUnmodifiableSet()),
                        entity.capacity - entity.parkedVehicles.stream()
                                .map(vehicle -> vehicle.size)
                                .reduce(0, Integer::sum),
                        entity.capacity,
                        entity.parkingSpotCategory))
                .filter(parkingSpotView -> parkingSpotView.getSpaceLeft() > 0)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotAdded event) {
        DATABASE.put(event.parkingSpotId(), new ParkingSpotViewEntity(
                event.parkingSpotId().getValue(),
                new HashSet<>(),
                event.capacity().getValue(),
                event.category()));
        log.debug("creating parking spot view with id {}", event.parkingSpotId());
    }

    @Override
    @ViewEventListener
    public void handle(Occupied event) {
        Option.of(DATABASE.get(event.getParkingSpotId()))
                .map(entity -> {
                    VehicleId vehicleId = event.getVehicleId();
                    SpotUnits spotUnits = event.getSpotUnits();
                    entity.parkedVehicles.add(new ParkedVehicleView(
                            vehicleId.getValue(),
                            spotUnits.getValue()));
                    return entity;
                });
        log.debug("updating parking spot view with id {} to decrease available capacity", event.getParkingSpotId());
    }

    @Override
    @ViewEventListener
    public void handle(Released event) {
        Option.of(DATABASE.get(event.getParkingSpotId()))
                .map(entity -> {
                    VehicleId vehicleId = event.getVehicleId();
                    entity.parkedVehicles.removeIf(vehicle -> vehicle.vehicleId.equals(vehicleId.getValue()));
                    return entity;
                });
        log.debug("updating parking spot view with id {} to increase available capacity", event.getParkingSpotId());
    }

}
