package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ReservationRequestsTimeSlotId {

    UUID value;

    public static ReservationRequestsTimeSlotId newOne() {
        return new ReservationRequestsTimeSlotId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
