package pl.cezarysanecki.parkingdomain.parking.vehicle.model;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VehicleFixture {

    public static Vehicle notParkedVehicle() {
        return new Vehicle(VehicleInformation.of(VehicleId.newOne(), SpotUnits.of(2)), Option.none());
    }

    public static Vehicle parkedVehicleOn(ParkingSpotId parkingSpotId) {
        return new Vehicle(VehicleInformation.of(VehicleId.newOne(), SpotUnits.of(2)), Option.of(parkingSpotId));
    }

}
