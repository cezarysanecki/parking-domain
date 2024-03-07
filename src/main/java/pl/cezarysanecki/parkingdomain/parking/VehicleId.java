package pl.cezarysanecki.parkingdomain.parking;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class VehicleId {

    @NonNull
    Long id;

}
