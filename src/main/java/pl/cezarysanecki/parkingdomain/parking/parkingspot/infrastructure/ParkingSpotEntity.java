package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure;

import io.vavr.API;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@AllArgsConstructor
class ParkingSpotEntity {

    final UUID parkingSpotId;
    final int capacity;
    Set<ParkingSpotVehicleEntity> vehicles;

    ParkingSpotEntity handle(ParkingSpotEvent domainEvent) {
        return API.Match(domainEvent).of(
                API.Case(API.$(instanceOf(ParkingSpotEvent.ParkingSpotOccupiedEvents.class)), this::handle),
                API.Case(API.$(instanceOf(ParkingSpotEvent.ParkingSpotOccupied.class)), this::handle),
                API.Case(API.$(instanceOf(ParkingSpotEvent.ParkingSpotLeftEvents.class)), this::handle),
                API.Case(API.$(instanceOf(ParkingSpotEvent.ParkingSpotLeft.class)), this::handle),
                Case($(), () -> this));
    }

    private ParkingSpotEntity handle(ParkingSpotEvent.ParkingSpotOccupiedEvents domainEvent) {
        ParkingSpotEvent.ParkingSpotOccupied event = domainEvent.getParkingSpotOccupied();
        return handle(event);
    }

    private ParkingSpotEntity handle(ParkingSpotEvent.ParkingSpotOccupied domainEvent) {
        VehicleId vehicleId = domainEvent.getVehicleId();
        VehicleSize vehicleSize = domainEvent.getVehicleSize();

        vehicles.add(new ParkingSpotVehicleEntity(vehicleId.getValue(), vehicleSize.getValue()));
        log.debug("occupying parking spot with id {} by vehicle with id {}", domainEvent.getParkingSpotId(), vehicleId);
        return this;
    }

    private ParkingSpotEntity handle(ParkingSpotEvent.ParkingSpotLeftEvents domainEvent) {
        ParkingSpotEvent.ParkingSpotLeft event = domainEvent.getParkingSpotLeft();
        return handle(event);
    }

    private ParkingSpotEntity handle(ParkingSpotEvent.ParkingSpotLeft domainEvent) {
        VehicleId vehicleId = domainEvent.getVehicleId();
        vehicles.removeIf(vehicle -> vehicle.vehicleId.equals(vehicleId.getValue()));
        log.debug("driving away vehicle with id {}", vehicleId);
        return this;
    }

}