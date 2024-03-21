package pl.cezarysanecki.parkingdomain.parking.application.parking;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;

@Value
public class ParkVehicleCommand {

    @NonNull ParkingSpotType parkingSpotType;
    @NonNull Vehicle vehicle;

}
