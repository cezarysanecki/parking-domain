package pl.cezarysanecki.parkingdomain.parking.view.vehicle.model;

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
