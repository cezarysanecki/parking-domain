package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure;

import io.vavr.API;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleSize;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotLeft;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotLeftEvents;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupied;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupiedEvents;

@Slf4j
@AllArgsConstructor
class ParkingSpotEntity {

    final UUID parkingSpotId;
    final int capacity;
    Set<ParkingSpotVehicleEntity> vehicles;

    ParkingSpotEntity handle(ParkingSpotEvent domainEvent) {
        return API.Match(domainEvent).of(
                API.Case(API.$(instanceOf(ParkingSpotOccupiedEvents.class)), this::handle),
                API.Case(API.$(instanceOf(ParkingSpotOccupied.class)), this::handle),
                API.Case(API.$(instanceOf(ParkingSpotLeftEvents.class)), this::handle),
                API.Case(API.$(instanceOf(ParkingSpotLeft.class)), this::handle),
                Case($(), () -> this));
    }

    private ParkingSpotEntity handle(ParkingSpotOccupiedEvents domainEvent) {
        ParkingSpotOccupied event = domainEvent.getParkingSpotOccupied();
        return handle(event);
    }

    private ParkingSpotEntity handle(ParkingSpotOccupied domainEvent) {
        VehicleId vehicleId = domainEvent.getVehicleId();
        VehicleSize vehicleSize = domainEvent.getVehicleSize();

        vehicles.add(new ParkingSpotVehicleEntity(vehicleId.getValue(), vehicleSize.getValue()));
        log.debug("occupying parking spot with id {} by vehicle with id {}", domainEvent.getParkingSpotId(), vehicleId);
        return this;
    }

    private ParkingSpotEntity handle(ParkingSpotLeftEvents domainEvent) {
        ParkingSpotLeft event = domainEvent.getParkingSpotLeft();
        return handle(event);
    }

    private ParkingSpotEntity handle(ParkingSpotLeft domainEvent) {
        VehicleId vehicleId = domainEvent.getVehicleId();
        vehicles.removeIf(vehicle -> vehicle.vehicleId.equals(vehicleId.getValue()));
        log.debug("driving away vehicle with id {}", vehicleId);
        return this;
    }

}
