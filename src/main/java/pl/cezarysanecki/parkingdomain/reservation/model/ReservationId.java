package pl.cezarysanecki.parkingdomain.reservation.model;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ReservationId {

    UUID value;

    @Override
    public String toString() {
        return value.toString();
    }

}
