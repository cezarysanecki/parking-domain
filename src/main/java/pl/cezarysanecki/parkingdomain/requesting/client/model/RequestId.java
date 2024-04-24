package pl.cezarysanecki.parkingdomain.requesting.client.model;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class RequestId {

    UUID value;

    public static RequestId newOne() {
        return new RequestId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
