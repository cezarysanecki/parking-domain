package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.NonNull;
import lombok.Value;

@Value
public class Vehicle {

    @NonNull VehicleId vehicleId;
    @NonNull VehicleSizeUnit vehicleSizeUnit;

}
