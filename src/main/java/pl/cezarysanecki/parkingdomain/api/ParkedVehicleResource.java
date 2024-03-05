package pl.cezarysanecki.parkingdomain.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class ParkedVehicleResource {

    @JsonProperty("vehicleId")
    Long vehicleId;
    @JsonProperty("parkingSpotId")
    Long parkingSpotId;

}
