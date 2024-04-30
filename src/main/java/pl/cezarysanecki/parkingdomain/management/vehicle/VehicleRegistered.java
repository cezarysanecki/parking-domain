package pl.cezarysanecki.parkingdomain.management.vehicle;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.client.ClientId;

public record VehicleRegistered(
        VehicleId vehicleId,
        VehicleSize vehicleSize,
        ClientId assignedTo
) implements DomainEvent {

    VehicleRegistered(Vehicle vehicle) {
        this(vehicle.getVehicleId(), vehicle.getVehicleSize(), vehicle.getAssignedTo());
    }

}
