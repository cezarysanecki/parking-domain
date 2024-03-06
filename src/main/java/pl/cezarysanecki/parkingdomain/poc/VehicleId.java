package pl.cezarysanecki.parkingdomain.poc;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class VehicleId {

    @NonNull
    Long id;

}
