package pl.cezarysanecki.parkingdomain.parking.vehicle.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleSize;

@Value(staticConstructor = "of")
public class VehicleInformation {

    @NonNull
    VehicleId vehicleId;
    @NonNull
    VehicleSize vehicleSize;

}
