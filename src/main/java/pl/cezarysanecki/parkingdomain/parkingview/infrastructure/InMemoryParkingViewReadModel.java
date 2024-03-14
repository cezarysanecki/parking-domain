package pl.cezarysanecki.parkingdomain.parkingview.infrastructure;

import io.vavr.API;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
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

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class InMemoryParkingViewReadModel implements ParkingViews {

    private final Map<ParkingSpotId, ParkingSpotViewEntityModel> database = new ConcurrentHashMap<>();

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

    @EventListener
    public void handle(ParkingSpotEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ParkingSpotCreated.class)), this::handle),
                Case($(instanceOf(VehicleLeft.class)), this::handle),
                Case($(instanceOf(VehicleParked.class)), this::handle),
                Case($(instanceOf(FullyOccupied.class)), this::handle),
                Case($(), () -> event));
    }

    public ParkingSpotEvent handle(ParkingSpotCreated parkingSpotCreated) {
        ParkingSpotId parkingSpotId = parkingSpotCreated.getParkingSpotId();
        database.put(parkingSpotId, new ParkingSpotViewEntityModel(parkingSpotId.getValue(), parkingSpotCreated.getCapacity()));
        log.debug("creating parking spot view with id {}", parkingSpotId);
        return parkingSpotCreated;
    }

    private ParkingSpotEvent handle(VehicleLeft vehicleLeft) {
        ParkingSpotId parkingSpotId = vehicleLeft.getParkingSpotId();
        Vehicle vehicle = vehicleLeft.getVehicle();

        ParkingSpotViewEntityModel entity = database.getOrDefault(
                parkingSpotId, new ParkingSpotViewEntityModel(parkingSpotId.getValue(), 0));
        entity.leftCapacity += vehicle.getVehicleSizeUnit().getValue();

        database.put(vehicleLeft.getParkingSpotId(), entity);
        log.debug("updating parking spot view with id {} for leaving vehicle", parkingSpotId);
        return vehicleLeft;
    }

    private ParkingSpotEvent handle(VehicleParked vehicleParked) {
        ParkingSpotId parkingSpotId = vehicleParked.getParkingSpotId();
        Vehicle vehicle = vehicleParked.getVehicle();

        ParkingSpotViewEntityModel entity = database.get(parkingSpotId);
        entity.leftCapacity -= vehicle.getVehicleSizeUnit().getValue();

        database.put(vehicleParked.getParkingSpotId(), entity);
        log.debug("updating parking spot view with id {} for parking vehicle", parkingSpotId);
        return vehicleParked;
    }

    private ParkingSpotEvent handle(FullyOccupied fullyOccupied) {
        database.remove(fullyOccupied.getParkingSpotId());
        log.debug("removing parking spot view with id {}", fullyOccupied.getParkingSpotId());
        return fullyOccupied;
    }

    @Data
    @AllArgsConstructor
    private static class ParkingSpotViewEntityModel {

        @NonNull
        UUID parkingSpotId;
        int leftCapacity;

    }


}

