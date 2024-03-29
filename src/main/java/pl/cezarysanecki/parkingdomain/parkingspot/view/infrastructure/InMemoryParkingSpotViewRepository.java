package pl.cezarysanecki.parkingdomain.parkingspot.view.infrastructure;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parkingspot.view.infrastructure.ParkingSpotViewEntity.ParkedVehicleView;
import pl.cezarysanecki.parkingdomain.parkingspot.view.model.ParkingSpotView;
import pl.cezarysanecki.parkingdomain.parkingspot.view.model.ParkingSpotViews;
import pl.cezarysanecki.parkingdomain.vehicle.model.Vehicle;
import pl.cezarysanecki.parkingdomain.vehicle.model.VehicleId;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static pl.cezarysanecki.parkingdomain.parkingspot.parking.application.CreatingParkingSpot.ParkingSpotCreated;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotLeft;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupied;

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
                        entity.capacity))
                .filter(parkingSpotView -> parkingSpotView.getSpaceLeft() > 0)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotCreated event) {
        DATABASE.put(event.getParkingSpotId(), new ParkingSpotViewEntity(
                event.getParkingSpotId().getValue(),
                new HashSet<>(),
                event.getParkingSpotCapacity().getValue()));
        log.debug("creating parking spot view with id {}", event.getParkingSpotId());
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotOccupied event) {
        Option.of(DATABASE.get(event.getParkingSpotId()))
                .map(entity -> {
                    Vehicle vehicle = event.getVehicle();
                    entity.parkedVehicles.add(new ParkedVehicleView(
                            vehicle.getVehicleId().getValue(),
                            vehicle.getVehicleSize().getValue()));
                    return entity;
                });
        log.debug("updating parking spot view with id {} to decrease available capacity", event.getParkingSpotId());
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotLeft event) {
        Option.of(DATABASE.get(event.getParkingSpotId()))
                .map(entity -> {
                    VehicleId vehicleId = event.getVehicleId();
                    entity.parkedVehicles.removeIf(vehicle -> vehicle.vehicleId.equals(vehicleId.getValue()));
                    return entity;
                });
        log.debug("updating parking spot view with id {} to increase available capacity", event.getParkingSpotId());
    }

}
