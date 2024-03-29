package pl.cezarysanecki.parkingdomain.parkingspot.view.infrastructure;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parkingspot.view.infrastructure.ParkingSpotViewEntity.ParkedVehicleView;
import pl.cezarysanecki.parkingdomain.parkingspot.view.model.ParkingSpotView;
import pl.cezarysanecki.parkingdomain.parkingspot.view.model.ParkingSpotViews;
import pl.cezarysanecki.parkingdomain.vehicle.model.Vehicle;
import pl.cezarysanecki.parkingdomain.vehicle.model.VehicleId;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static pl.cezarysanecki.parkingdomain.parkingspot.parking.application.CreatingParkingSpot.ParkingSpotCreated;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotLeft;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupied;

class InMemoryParkingSpotViewRepository implements ParkingSpotViews {

    private static final Map<ParkingSpotId, ParkingSpotViewEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public Set<ParkingSpotView> queryForAvailableParkingSpots() {
        return DATABASE.values().stream()
                .map(entity -> new ParkingSpotView(
                        entity.getParkingSpotId(),
                        entity.getParkedVehicles().stream()
                                .map(vehicle -> new ParkingSpotView.ParkedVehicleView(vehicle.getVehicleId()))
                                .collect(Collectors.toUnmodifiableSet()),
                        entity.getCapacity() - entity.getParkedVehicles().stream()
                                .map(ParkedVehicleView::getSize)
                                .reduce(0, Integer::sum),
                        entity.getCapacity()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotCreated event) {
        DATABASE.put(event.getParkingSpotId(), new ParkingSpotViewEntity(
                event.getParkingSpotId().getValue(),
                Set.of(),
                event.getParkingSpotCapacity().getValue()));
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotOccupied event) {
        Option.of(DATABASE.get(event.getParkingSpotId()))
                .map(entity -> {
                    Vehicle vehicle = event.getVehicle();
                    entity.getParkedVehicles().add(new ParkedVehicleView(
                            vehicle.getVehicleId().getValue(),
                            vehicle.getVehicleSize().getValue()));
                    return entity;
                });
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotLeft event) {
        Option.of(DATABASE.get(event.getParkingSpotId()))
                .map(entity -> {
                    VehicleId vehicleId = event.getVehicleId();
                    entity.getParkedVehicles().removeIf(vehicle -> vehicle.getVehicleId().equals(vehicleId.getValue()));
                    return entity;
                });
    }

}
