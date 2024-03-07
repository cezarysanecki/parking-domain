package pl.cezarysanecki.parkingdomain.availability.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.model.VehicleType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;

@Value
public class AssignmentCommand {

    @NonNull VehicleId vehicleId;
    @NonNull VehicleType vehicleType;

}
