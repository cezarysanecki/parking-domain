package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;

@Value
public class ParkVehicleCommand {

    @NonNull ParkingSpotId parkingSpotId;
    @NonNull Vehicle vehicle;

}
