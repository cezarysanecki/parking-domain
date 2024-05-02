package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure;

import io.vavr.API;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.Released;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ReleasedEvents;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.Occupied;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupiedEvents;

@Slf4j
@AllArgsConstructor
class ParkingSpotEntity {

    final UUID parkingSpotId;
    final int capacity;
    Set<ParkingSpotVehicleEntity> vehicles;

    ParkingSpotEntity handle(ParkingSpotEvent domainEvent) {
        return API.Match(domainEvent).of(
                API.Case(API.$(instanceOf(OccupiedEvents.class)), this::handle),
                API.Case(API.$(instanceOf(Occupied.class)), this::handle),
                API.Case(API.$(instanceOf(ReleasedEvents.class)), this::handle),
                API.Case(API.$(instanceOf(Released.class)), this::handle),
                Case($(), () -> this));
    }

    private ParkingSpotEntity handle(OccupiedEvents domainEvent) {
        Occupied event = domainEvent.getOccupied();
        return handle(event);
    }

    private ParkingSpotEntity handle(Occupied domainEvent) {
        VehicleId vehicleId = domainEvent.getVehicleId();
        SpotUnits spotUnits = domainEvent.getSpotUnits();

        vehicles.add(new ParkingSpotVehicleEntity(vehicleId.getValue(), spotUnits.getValue()));
        log.debug("occupying parking spot with id {} by vehicle with id {}", domainEvent.getParkingSpotId(), vehicleId);
        return this;
    }

    private ParkingSpotEntity handle(ReleasedEvents domainEvent) {
        Released event = domainEvent.getReleased();
        return handle(event);
    }

    private ParkingSpotEntity handle(Released domainEvent) {
        VehicleId vehicleId = domainEvent.getVehicleId();
        vehicles.removeIf(vehicle -> vehicle.vehicleId.equals(vehicleId.getValue()));
        log.debug("driving away vehicle with id {}", vehicleId);
        return this;
    }

}
