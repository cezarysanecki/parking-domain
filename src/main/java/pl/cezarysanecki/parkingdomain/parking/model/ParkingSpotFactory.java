package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotFactory {

    public static OpenParkingSpot createOpen(ParkingSpotBase parkingSpot) {
        return new OpenParkingSpot(
                parkingSpot,
                OpenParkingSpotPolicy.allCurrentPolicies());
    }

    public static ReservedParkingSpot createReserved(ParkingSpotBase parkingSpot, ReservationId reservationId) {
        return new ReservedParkingSpot(
                parkingSpot,
                reservationId,
                ReservedParkingSpotPolicy.allCurrentPolicies());
    }

    public static OccupiedParkingSpot createOccupied(ParkingSpotBase parkingSpot) {
        return new OccupiedParkingSpot(parkingSpot);
    }

}
