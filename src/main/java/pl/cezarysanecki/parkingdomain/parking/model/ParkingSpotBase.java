package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.Value;

@Value
public class ParkingSpotBase {

    ParkingSpotId parkingSpotId;
    ParkingSpotCapacity capacity;
    ParkedVehicles parkedVehicles;
    boolean outOfOrder;

    public boolean isParked(VehicleId vehicleId) {
        return parkedVehicles.contains(vehicleId);
    }

    public boolean isExceededWith(Vehicle vehicle) {
        return parkedVehicles.occupation() + vehicle.getVehicleSizeUnit().getValue() > capacity.getValue();
    }

    public boolean isFullyOccupiedWith(Vehicle vehicle) {
        return parkedVehicles.occupation() + vehicle.getVehicleSizeUnit().getValue() == capacity.getValue();
    }

    public boolean isEmpty() {
        return parkedVehicles.isEmpty();
    }

    public boolean isFull() {
        return parkedVehicles.occupation() == capacity.getValue();
    }

}
