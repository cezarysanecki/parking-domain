package pl.cezarysanecki.parkingdomain.reserving.client.model;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ClientId {

    UUID value;

    @Override
    public String toString() {
        return value.toString();
    }

}