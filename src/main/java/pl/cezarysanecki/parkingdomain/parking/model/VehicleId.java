package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class VehicleId {

    @NonNull
    Long id;

}
