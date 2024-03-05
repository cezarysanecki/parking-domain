package pl.cezarysanecki.parkingdomain.model;

import lombok.Value;

import java.util.List;

@Value
public class ParkedVehicleTypesOnParkingSpot {

    List<VehicleType> vehicleTypes;

    public boolean isFullyOccupiedByCars() {
        return vehicleTypes.size() == 1 && vehicleTypes.get(0) == VehicleType.CAR;
    }

    public boolean isFullyOccupiedByMotorcycles() {
        return vehicleTypes.size() == 2 && vehicleTypes.stream().allMatch(type -> type == VehicleType.MOTORCYCLE);
    }

    public boolean isFullyOccupiedByBikesOrScooters() {
        return vehicleTypes.size() == 3 && vehicleTypes.stream().allMatch(type -> type == VehicleType.BIKE || type == VehicleType.SCOOTER);
    }

    public boolean contains(VehicleType vehicleType) {
        return vehicleTypes.contains(vehicleType);
    }

}
