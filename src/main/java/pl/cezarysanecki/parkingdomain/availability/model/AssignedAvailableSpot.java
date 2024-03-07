package pl.cezarysanecki.parkingdomain.availability.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;

import java.time.Instant;
import java.util.UUID;

@Value
public class AssignedAvailableSpot implements DomainEvent {

    @NonNull UUID eventId = UUID.randomUUID();
    @NonNull Instant when = Instant.now();

    @NonNull ParkingSpotId parkingSpotId;
    @NonNull VehicleId vehicleId;

}
