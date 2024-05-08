package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class OccupationId {

    UUID value;

    public static OccupationId newOne() {
        return new OccupationId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
