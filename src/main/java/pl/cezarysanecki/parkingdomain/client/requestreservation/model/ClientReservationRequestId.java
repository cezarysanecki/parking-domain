package pl.cezarysanecki.parkingdomain.client.requestreservation.model;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ClientReservationRequestId {

    @NonNull
    UUID value;

    @Override
    public String toString() {
        return value.toString();
    }

}
