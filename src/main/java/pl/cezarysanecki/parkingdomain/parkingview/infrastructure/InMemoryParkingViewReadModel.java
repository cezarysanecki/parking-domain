package pl.cezarysanecki.parkingdomain.parkingview.infrastructure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parkingview.model.AvailableParkingSpotView;
import pl.cezarysanecki.parkingdomain.parkingview.model.AvailableParkingSpotsView;
import pl.cezarysanecki.parkingdomain.parkingview.model.ParkingViews;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
class InMemoryParkingViewReadModel implements ParkingViews {

    private final Map<ParkingSpotId, ParkingSpotViewModel> database = new ConcurrentHashMap<>();

    @EventListener
    public void handle(ParkingSpotCreated parkingSpotCreated) {
        ParkingSpotId parkingSpotId = parkingSpotCreated.getParkingSpotId();
        database.put(parkingSpotId, new ParkingSpotViewModel(parkingSpotId.getValue(), parkingSpotCreated.getCapacity()));
    }

    @EventListener
    public void handle(VehicleLeft vehicleLeft) {
        ParkingSpotId parkingSpotId = vehicleLeft.getParkingSpotId();
        Vehicle vehicle = vehicleLeft.getVehicle();

        ParkingSpotViewModel parkingSpotViewModel = database.getOrDefault(
                parkingSpotId, new ParkingSpotViewModel(parkingSpotId.getValue(), 0));
        parkingSpotViewModel.leftCapacity += vehicle.getVehicleSizeUnit().getValue();

        database.put(parkingSpotId, parkingSpotViewModel);
    }

    @EventListener
    public void handle(VehicleParked vehicleParked) {
        ParkingSpotId parkingSpotId = vehicleParked.getParkingSpotId();
        Vehicle vehicle = vehicleParked.getVehicle();

        ParkingSpotViewModel parkingSpotViewModel = database.get(parkingSpotId);
        parkingSpotViewModel.leftCapacity -= vehicle.getVehicleSizeUnit().getValue();

        database.put(parkingSpotId, parkingSpotViewModel);
    }

    @EventListener
    public void handle(FullyOccupied fullyOccupied) {
        database.remove(fullyOccupied.getParkingSpotId());
    }

    @Override
    public AvailableParkingSpotsView findAvailable() {
        return new AvailableParkingSpotsView(database.values()
                .stream()
                .map(model -> new AvailableParkingSpotView(
                        ParkingSpotId.of(model.parkingSpotId),
                        model.leftCapacity
                ))
                .collect(Collectors.toUnmodifiableSet()));
    }

    @Data
    @AllArgsConstructor
    private static class ParkingSpotViewModel {

        @NonNull
        UUID parkingSpotId;
        int leftCapacity;

    }


}

