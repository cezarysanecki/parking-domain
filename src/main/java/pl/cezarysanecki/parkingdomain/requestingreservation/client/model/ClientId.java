package pl.cezarysanecki.parkingdomain.requestingreservation.client.model;

import lombok.Value;

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
