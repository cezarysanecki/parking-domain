package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot;

import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotReservationRequestsFixture {

    public static ReservationRequestsTimeSlot parkingSpotWithoutReservationRequests() {
        return ReservationRequestsTimeSlot.newOne(
                ParkingSpotId.newOne(),
                ReservationRequestsTimeSlotId.newOne(),
                ParkingSpotCapacity.of(4));
    }

    public static ReservationRequestsTimeSlot parkingSpotFullyRequested() {
        ParkingSpotCapacity capacity = ParkingSpotCapacity.of(4);
        ReservationRequest reservationRequest = ReservationRequest.newOne(
                ReservationRequesterId.newOne(), SpotUnits.of(capacity.getValue()));

        return new ReservationRequestsTimeSlot(
                ParkingSpotId.newOne(),
                ReservationRequestsTimeSlotId.newOne(),
                capacity,
                HashMap.of(reservationRequest.reservationRequestId(), reservationRequest),
                Version.zero());
    }

    public static ReservationRequestsTimeSlot parkingSpotWithRequest(ReservationRequest reservationRequest) {
        return new ReservationRequestsTimeSlot(
                ParkingSpotId.newOne(),
                ReservationRequestsTimeSlotId.newOne(),
                ParkingSpotCapacity.of(4),
                HashMap.of(reservationRequest.reservationRequestId(), reservationRequest),
                Version.zero());
    }

}
