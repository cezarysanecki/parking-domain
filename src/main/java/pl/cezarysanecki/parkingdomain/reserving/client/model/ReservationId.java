package pl.cezarysanecki.parkingdomain.reserving.client.model;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ReservationId {

    UUID value;

}
