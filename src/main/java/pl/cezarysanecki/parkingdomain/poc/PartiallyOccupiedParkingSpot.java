package pl.cezarysanecki.parkingdomain.poc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PartiallyOccupiedParkingSpot implements OccupiedParkingSpot {

    private final ParkingSpotId parkingSpotId;
    private final VehicleType parkedVehicleType;
    private final List<VehicleId> parkedVehicles;

    PartiallyOccupiedParkingSpot(
            ParkingSpotId parkingSpotId,
            VehicleType parkedVehicleType,
            List<VehicleId> parkedVehicles) {
        this.parkingSpotId = parkingSpotId;
        this.parkedVehicleType = parkedVehicleType;
        this.parkedVehicles = new ArrayList<>(parkedVehicles);
    }

    ParkingSpot grantAccessTo(Vehicle vehicle) {
        VehicleId vehicleId = vehicle.getVehicleId();
        VehicleType vehicleType = vehicle.getVehicleType();

        parkedVehicles.add(vehicleId);

        if (parkedVehicles.size() == parkedVehicleType.getAllowedVehiclesOnSpot()) {
            return new FullyOccupiedParkingSpot(parkingSpotId, parkedVehicles());
        }
        return new PartiallyOccupiedParkingSpot(parkingSpotId, vehicleType, parkedVehicles());
    }

    @Override
    public ParkingSpotId parkingSpotId() {
        return parkingSpotId;
    }

    @Override
    public List<VehicleId> parkedVehicles() {
        return Collections.unmodifiableList(parkedVehicles);
    }

}
