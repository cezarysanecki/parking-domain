package pl.cezarysanecki.parkingdomain.requestingreservation.model.template;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ParkingSpotReservationRequestsTemplateId {

    UUID value;

    public static ParkingSpotReservationRequestsTemplateId newOne() {
        return new ParkingSpotReservationRequestsTemplateId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
