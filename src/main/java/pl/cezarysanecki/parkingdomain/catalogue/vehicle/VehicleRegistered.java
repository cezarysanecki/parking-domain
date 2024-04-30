package pl.cezarysanecki.parkingdomain.catalogue.vehicle;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.catalogue.client.ClientId;

public record VehicleRegistered(
        VehicleId vehicleId,
        VehicleSize vehicleSize,
        ClientId assignedTo
) implements DomainEvent {

    VehicleRegistered(Vehicle vehicle) {
        this(vehicle.getVehicleId(), vehicle.getVehicleSize(), vehicle.getAssignedTo());
    }

}
