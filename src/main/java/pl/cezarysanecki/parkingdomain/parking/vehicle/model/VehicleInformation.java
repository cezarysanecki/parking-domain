package pl.cezarysanecki.parkingdomain.parking.vehicle.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;

@Value(staticConstructor = "of")
public class VehicleInformation {

    @NonNull
    VehicleId vehicleId;
    @NonNull
    SpotUnits spotUnits;

}
