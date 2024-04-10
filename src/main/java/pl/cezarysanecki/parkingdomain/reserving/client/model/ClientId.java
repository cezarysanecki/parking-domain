package pl.cezarysanecki.parkingdomain.reserving.client.model;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ClientId {

    UUID value;

    public static ClientId newOne() {
        return new ClientId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
