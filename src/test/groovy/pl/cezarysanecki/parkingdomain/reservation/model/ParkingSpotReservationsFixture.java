package pl.cezarysanecki.parkingdomain.reservation.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotReservationsFixture {

    public static ParkingSpotReservations emptyParkingSpotReservations(ParkingSpotId parkingSpotId) {
        return new ParkingSpotReservations(parkingSpotId, Map.of());
    }

    public static ParkingSpotReservations parkingSpotReservationsWith(ParkingSpotId parkingSpotId, ReservationPeriod.DayPart dayPart, DayPartReservations dayPartReservations) {
        return new ParkingSpotReservations(parkingSpotId, Map.of(dayPart, dayPartReservations));
    }

    public static Reservation individual(ReservationId reservationId) {
        return new Reservation.Individual(reservationId);
    }

    public static Reservation collective(ReservationId reservationId) {
        return new Reservation.Collective(reservationId);
    }

    public static ReservationId anyReservationId() {
        return ReservationId.of(UUID.randomUUID());
    }

}
