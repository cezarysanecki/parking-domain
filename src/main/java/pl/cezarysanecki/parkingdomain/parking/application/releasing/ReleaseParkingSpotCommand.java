package pl.cezarysanecki.parkingdomain.parking.application.releasing;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;

@Value
public class ReleaseParkingSpotCommand {

    @NonNull VehicleId vehicleId;

}
