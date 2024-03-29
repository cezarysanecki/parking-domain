package pl.cezarysanecki.parkingdomain.vehicle.view.model;

import lombok.NonNull;
import lombok.Value;
import org.springframework.lang.Nullable;

import java.util.UUID;

@Value
public class VehicleView {

    @NonNull
    UUID vehicleId;
    @Nullable
    UUID parkingSpotId;

}
