package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;

@Value
public class ReleaseParkingSpotCommand {

    @NonNull VehicleId vehicleId;

}
