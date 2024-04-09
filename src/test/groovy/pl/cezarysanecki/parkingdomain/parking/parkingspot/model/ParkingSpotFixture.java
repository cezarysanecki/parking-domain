package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotFixture {

    public static OccupiedParkingSpot fullyOccupiedBy(VehicleId vehicleId) {
        return new OccupiedParkingSpot(
                ParkingSpotInformation.of(
                        ParkingSpotId.newOne(),
                        ParkingSpotOccupation.of(0, 4)),
                Set.of(vehicleId));
    }

    public static OccupiedParkingSpot occupiedBy(VehicleId... vehicleIds) {
        return new OccupiedParkingSpot(
                ParkingSpotInformation.of(
                        ParkingSpotId.newOne(),
                        ParkingSpotOccupation.of(0, 4)),
                Set.of(vehicleIds));
    }

}
