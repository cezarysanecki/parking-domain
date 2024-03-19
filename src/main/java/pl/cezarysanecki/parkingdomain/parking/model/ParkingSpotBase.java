package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkedVehicles;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;

@Value
public class ParkingSpotBase {

    ParkingSpotId parkingSpotId;
    ParkingSpotCapacity capacity;
    ParkedVehicles parkedVehicles;
    boolean outOfOrder;

    public boolean isParked(VehicleId vehicleId) {
        return parkedVehicles.contains(vehicleId);
    }

    public boolean thereIsEnoughSpaceFor(Vehicle vehicle) {
        return parkedVehicles.occupation() + vehicle.getVehicleSizeUnit().getValue() <= capacity.getValue();
    }

    public boolean isFullyOccupiedWith(Vehicle vehicle) {
        return parkedVehicles.occupation() + vehicle.getVehicleSizeUnit().getValue() == capacity.getValue();
    }

}
