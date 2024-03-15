package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;

import java.time.Instant;

@Value
public class ParkVehicleCommand {

    @NonNull ClientId clientId;
    @NonNull ParkingSpotId parkingSpotId;
    @NonNull Vehicle vehicle;
    @NonNull Instant when;

}
