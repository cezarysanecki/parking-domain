package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.model.SpotOccupation;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotRequestsFixture {

    public static ParkingSpotReservationRequests parkingSpotWithoutRequests() {
        return new ParkingSpotReservationRequests(
                ParkingSpotId.newOne(), SpotOccupation.of(0, 4), Set.of());
    }

    public static ParkingSpotReservationRequests parkingSpotWithoutPlaceForRequestsWithCapacity(int capacity) {
        return new ParkingSpotReservationRequests(
                ParkingSpotId.newOne(), SpotOccupation.of(0, capacity), Set.of());
    }

    public static ParkingSpotReservationRequests parkingSpotWithoutPlaceForAnyRequests(ReservationRequestId reservationRequestId) {
        return new ParkingSpotReservationRequests(
                ParkingSpotId.newOne(), SpotOccupation.of(4, 4), Set.of(reservationRequestId));
    }

}
