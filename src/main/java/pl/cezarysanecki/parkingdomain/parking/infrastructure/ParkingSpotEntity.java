package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.API;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeftEvents;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ParkingSpotEntity {

    @Id
    Long id;
    UUID parkingSpotId;
    ParkingSpotType parkingSpotType;
    int capacity;
    Set<ParkedVehicleEntity> parkedVehicles;
    Option<UUID> reservation;
    boolean outOfOrder;

    ParkingSpotEntity(UUID parkingSpotId, ParkingSpotType parkingSpotType, int capacity) {
        this.parkingSpotId = parkingSpotId;
        this.parkingSpotType = parkingSpotType;
        this.capacity = capacity;
        this.parkedVehicles = new HashSet<>();
        this.reservation = Option.none();
        this.outOfOrder = false;
    }

    ParkingSpotEntity handle(ParkingSpotEvent event) {
        return API.Match(event).of(
                Case($(instanceOf(VehicleParkedEvents.class)), this::handle),
                Case($(instanceOf(VehicleParked.class)), this::handle),
                Case($(instanceOf(VehicleLeft.class)), this::handle),
                Case($(instanceOf(VehicleLeftEvents.class)), this::handle),
                Case($(), () -> this));
    }

    boolean hasEnoughSpace(final VehicleSizeUnit vehicleSizeUnit) {
        Integer currentOccupation = parkedVehicles.stream()
                .map(ParkedVehicleEntity::getVehicleSizeUnit)
                .reduce(0, Integer::sum);
        return capacity - currentOccupation >= vehicleSizeUnit.getValue();
    }

    private ParkingSpotEntity handle(VehicleParkedEvents vehicleParkedEvents) {
        return handle(vehicleParkedEvents.getVehicleParked());
    }

    private ParkingSpotEntity handle(VehicleParked vehicleParked) {
        Vehicle vehicle = vehicleParked.getVehicle();
        parkedVehicles.add(new ParkedVehicleEntity(
                parkingSpotId, vehicle.getVehicleId().getValue(), vehicle.getVehicleSizeUnit().getValue()));
        log.debug("parked vehicle with id {}", vehicle.getVehicleId());
        return this;
    }

    private ParkingSpotEntity handle(VehicleLeftEvents vehicleLeftEvents) {
        return handle(vehicleLeftEvents.getVehiclesLeft());
    }

    private ParkingSpotEntity handle(VehicleLeft vehicleLeft) {
        UUID vehicleId = vehicleLeft.getVehicle().getVehicleId().getValue();
        return removeVehicleIfPresent(parkingSpotId, vehicleId);
    }

    private ParkingSpotEntity removeVehicleIfPresent(UUID parkingSpotId, UUID vehicleId) {
        parkedVehicles.stream()
                .filter(entity -> entity.is(parkingSpotId, vehicleId))
                .findAny()
                .ifPresent(entity -> parkedVehicles.remove(entity));
        log.debug("released vehicle with id {}", vehicleId);
        return this;
    }

}
