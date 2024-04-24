package pl.cezarysanecki.parkingdomain.requesting.parkingspot.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotReservationsFixture {

    public static ParkingSpotRequests parkingSpotWithoutReservationRequests() {
        return new ParkingSpotRequests(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(0, 4), Set.of());
    }

    public static ParkingSpotRequests parkingSpotWithoutPlaceForReservationRequestsWithCapacity(int capacity) {
        return new ParkingSpotRequests(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(0, capacity), Set.of());
    }

    public static ParkingSpotRequests parkingSpotWithoutPlaceForReservationRequests(ReservationId reservationId) {
        return new ParkingSpotRequests(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(4, 4), Set.of(reservationId));
    }

}
