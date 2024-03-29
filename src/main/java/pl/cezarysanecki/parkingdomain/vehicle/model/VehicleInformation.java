package pl.cezarysanecki.parkingdomain.vehicle.model;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class VehicleInformation {

    @NonNull VehicleId vehicleId;
    @NonNull RegistrationNumber registrationNumber;
    @NonNull VehicleSize vehicleSize;

}
