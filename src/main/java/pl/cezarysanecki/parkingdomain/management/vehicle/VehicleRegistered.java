package pl.cezarysanecki.parkingdomain.management.vehicle;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.client.ClientId;

public record VehicleRegistered(
        VehicleId vehicleId,
        SpotUnits spotUnits,
        ClientId assignedTo
) implements DomainEvent {

    VehicleRegistered(Vehicle vehicle) {
        this(vehicle.getVehicleId(), vehicle.getSpotUnits(), vehicle.getAssignedTo());
    }

}
