package pl.cezarysanecki.parkingdomain.parking.application.parking;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;

@Value
public class ParkVehicleCommand {

    @NonNull ParkingSpotId parkingSpotId;
    @NonNull ParkingSpotType parkingSpotType;
    @NonNull Vehicle vehicle;

}
