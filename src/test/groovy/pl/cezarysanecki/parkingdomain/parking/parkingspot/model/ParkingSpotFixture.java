package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotFixture {

    public static ParkingSpot fullyOccupiedBy(VehicleId vehicleId) {
        return new ParkingSpot(
                ParkingSpotId.newOne(),
                ParkingSpotOccupation.of(0, 4),
                Set.of(vehicleId));
    }

    public static ParkingSpot occupiedBy(VehicleId... vehicleIds) {
        return new ParkingSpot(
                ParkingSpotId.newOne(),
                ParkingSpotOccupation.of(0, 4),
                Set.of(vehicleIds));
    }

    public static ParkingSpot emptyOpenParkingSpotWithCapacity(int capacity) {
        return new ParkingSpot(
                ParkingSpotId.newOne(),
                ParkingSpotOccupation.of(0, capacity),
                Set.of());
    }

}
