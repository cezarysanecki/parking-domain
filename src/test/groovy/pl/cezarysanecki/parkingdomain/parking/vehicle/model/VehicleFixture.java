package pl.cezarysanecki.parkingdomain.parking.vehicle.model;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleSize;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VehicleFixture {

    public static Vehicle notParkedVehicle() {
        return new Vehicle(VehicleInformation.of(VehicleId.newOne(), VehicleSize.of(2)), Option.none());
    }

    public static Vehicle parkedVehicleOn(ParkingSpotId parkingSpotId) {
        return new Vehicle(VehicleInformation.of(VehicleId.newOne(), VehicleSize.of(2)), Option.of(parkingSpotId));
    }

}
