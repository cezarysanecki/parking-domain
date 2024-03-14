package pl.cezarysanecki.parkingdomain.reservation.model;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ClientId {

    @NonNull
    UUID value;

    @Override
    public String toString() {
        return value.toString();
    }

}
