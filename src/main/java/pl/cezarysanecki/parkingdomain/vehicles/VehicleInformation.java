package pl.cezarysanecki.parkingdomain.vehicles;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class VehicleInformation {

    @NonNull VehicleId vehicleId;
    @NonNull VehicleSize vehicleSize;

}
