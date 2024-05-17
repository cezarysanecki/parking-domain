package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotReservationRequestsFixture {

    public static ParkingSpotReservationRequests parkingSpotWithoutReservationRequests() {
        return ParkingSpotReservationRequests.newOne(
                ParkingSpotId.newOne(),
                ParkingSpotCapacity.of(4));
    }

    public static ParkingSpotReservationRequests parkingSpotFullyRequested() {
        ParkingSpotCapacity capacity = ParkingSpotCapacity.of(4);
        ReservationRequest reservationRequest = ReservationRequest.newOne(
                ReservationRequesterId.newOne(), SpotUnits.of(capacity.getValue()));

        return new ParkingSpotReservationRequests(
                ParkingSpotId.newOne(),
                capacity,
                HashMap.of(reservationRequest.getReservationRequestId(), reservationRequest),
                Version.zero());
    }

    public static ParkingSpotReservationRequests parkingSpotWithRequest(ReservationRequest reservationRequest) {
        return new ParkingSpotReservationRequests(
                ParkingSpotId.newOne(),
                ParkingSpotCapacity.of(4),
                HashMap.of(reservationRequest.getReservationRequestId(), reservationRequest),
                Version.zero());
    }

}
