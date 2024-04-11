package pl.cezarysanecki.parkingdomain.reserving.parkingspot.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotReservationsFixture {

    public static ParkingSpotReservations noParkingSpotReservations() {
        return new ParkingSpotReservations(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(0, 4), Set.of());
    }

    public static ParkingSpotReservations noParkingSpotReservationsWithCapacity(int capacity) {
        return new ParkingSpotReservations(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(0, capacity), Set.of());
    }

    public static ParkingSpotReservations fullyReservedParkingSpotBy(ReservationId reservationId) {
        return new ParkingSpotReservations(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(4, 4), Set.of(reservationId));
    }

}
