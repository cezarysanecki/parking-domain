package pl.cezarysanecki.parkingdomain.parkingspot.parking.infrastructure;

import io.vavr.API;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotLeftEvents;
import pl.cezarysanecki.parkingdomain.vehicle.model.VehicleInformation;
import pl.cezarysanecki.parkingdomain.vehicle.model.VehicleId;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotLeft;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupied;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupiedEvents;

@Slf4j
@AllArgsConstructor
class ParkingSpotEntity {

    final UUID parkingSpotId;
    final int capacity;
    Set<ParkingSpotVehicleEntity> vehicles;

    ParkingSpotEntity handle(ParkingSpotEvent parkingSpotEvent) {
        return API.Match(parkingSpotEvent).of(
                Case($(instanceOf(ParkingSpotOccupiedEvents.class)), this::handle),
                Case($(instanceOf(ParkingSpotOccupied.class)), this::handle),
                Case($(instanceOf(ParkingSpotLeftEvents.class)), this::handle),
                Case($(instanceOf(ParkingSpotLeft.class)), this::handle),
                Case($(), () -> this));
    }

    private ParkingSpotEntity handle(ParkingSpotOccupiedEvents parkingSpotOccupiedEvents) {
        ParkingSpotOccupied event = parkingSpotOccupiedEvents.getParkingSpotOccupied();
        return handle(event);
    }

    private ParkingSpotEntity handle(ParkingSpotOccupied parkingSpotOccupied) {
        VehicleInformation vehicle = parkingSpotOccupied.getVehicle();
        vehicles.add(new ParkingSpotVehicleEntity(vehicle.getVehicleId().getValue(), vehicle.getVehicleSize().getValue()));
        log.debug("occupying parking spot with id {} by vehicle with id {}", parkingSpotOccupied.getParkingSpotId(), vehicle.getVehicleId());
        return this;
    }

    private ParkingSpotEntity handle(ParkingSpotLeftEvents parkingSpotLeftEvents) {
        ParkingSpotLeft event = parkingSpotLeftEvents.getParkingSpotLeft();
        return handle(event);
    }

    private ParkingSpotEntity handle(ParkingSpotLeft parkingSpotLeft) {
        VehicleId vehicleId = parkingSpotLeft.getVehicleId();
        vehicles.removeIf(vehicle -> vehicle.vehicleId.equals(vehicleId.getValue()));
        log.debug("driving away vehicle with id {}", vehicleId);
        return this;
    }

}
