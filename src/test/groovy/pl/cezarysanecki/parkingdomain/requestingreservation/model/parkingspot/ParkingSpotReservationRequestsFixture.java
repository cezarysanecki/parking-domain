package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotReservationRequestsFixture {

    public static ParkingSpotReservationRequests parkingSpotWithoutReservationRequests() {
        return ParkingSpotReservationRequests.newOne(
                ParkingSpotId.newOne(),
                ParkingSpotCapacity.of(4),
                TimeSlot.createTimeSlotAtUTC(LocalDate.now(), 7, 17));
    }

    public static ParkingSpotReservationRequests parkingSpotFullyRequested() {
        ParkingSpotCapacity capacity = ParkingSpotCapacity.of(4);
        ReservationRequest reservationRequest = ReservationRequest.newOne(
                ReservationRequesterId.newOne(), SpotUnits.of(capacity.getValue()));

        return new ParkingSpotReservationRequests(
                ParkingSpotTimeSlotId.newOne(),
                ParkingSpotId.newOne(),
                capacity,
                HashMap.of(reservationRequest.getReservationRequestId(), reservationRequest),
                TimeSlot.createTimeSlotAtUTC(LocalDate.now(), 7, 17),
                Version.zero());
    }

    public static ParkingSpotReservationRequests parkingSpotWithRequest(ReservationRequest reservationRequest) {
        return new ParkingSpotReservationRequests(
                ParkingSpotTimeSlotId.newOne(),
                ParkingSpotId.newOne(),
                ParkingSpotCapacity.of(4),
                HashMap.of(reservationRequest.getReservationRequestId(), reservationRequest),
                TimeSlot.createTimeSlotAtUTC(LocalDate.now(), 7, 17),
                Version.zero());
    }

}
