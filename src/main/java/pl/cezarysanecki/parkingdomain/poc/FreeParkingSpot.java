package pl.cezarysanecki.parkingdomain.poc;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
class FreeParkingSpot implements ParkingSpot {

    private final ParkingSpotId parkingSpotId;

    ParkingSpot grantAccessTo(Vehicle vehicle) {
        VehicleId vehicleId = vehicle.getVehicleId();
        VehicleType vehicleType = vehicle.getVehicleType();

        if (vehicleType.getAllowedVehiclesOnSpot() == 1) {
            return new FullyOccupiedParkingSpot(parkingSpotId, List.of(vehicleId));
        }
        return new PartiallyOccupiedParkingSpot(parkingSpotId, vehicleType, List.of(vehicleId));
    }

    @Override
    public ParkingSpotId parkingSpotId() {
        return parkingSpotId;
    }

}
