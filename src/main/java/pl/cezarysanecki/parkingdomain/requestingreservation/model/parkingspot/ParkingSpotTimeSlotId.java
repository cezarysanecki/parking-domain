package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ParkingSpotTimeSlotId {

    UUID value;

    public static ParkingSpotTimeSlotId newOne() {
        return new ParkingSpotTimeSlotId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
