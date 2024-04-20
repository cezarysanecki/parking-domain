package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotReservationsFixture {

    public static ParkingSpotReservationRequests parkingSpotWithoutReservationRequests() {
        return new ParkingSpotReservationRequests(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(0, 4), Set.of());
    }

    public static ParkingSpotReservationRequests parkingSpotWithoutPlaceForReservationRequestsWithCapacity(int capacity) {
        return new ParkingSpotReservationRequests(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(0, capacity), Set.of());
    }

    public static ParkingSpotReservationRequests parkingSpotWithoutPlaceForReservationRequests(ReservationId reservationId) {
        return new ParkingSpotReservationRequests(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(4, 4), Set.of(reservationId));
    }

}
