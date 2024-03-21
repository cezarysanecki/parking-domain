package pl.cezarysanecki.parkingdomain.parking.model.parking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotBase;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservedParkingSpotFactory {

    public static ReservedParkingSpot create(ParkingSpotBase parkingSpot, ReservationId reservationId) {
        return new ReservedParkingSpot(
                parkingSpot,
                reservationId,
                ReservedParkingSpotPolicy.allCurrentPolicies());
    }

}
