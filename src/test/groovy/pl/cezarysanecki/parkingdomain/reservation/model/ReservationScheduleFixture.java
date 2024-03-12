package pl.cezarysanecki.parkingdomain.reservation.model;

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

import java.time.LocalDateTime;
import java.util.Set;

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId;

public class ReservationScheduleFixture {

    public static ReservationSchedule emptyReservationSchedule(ParkingSpotId parkingSpotId, LocalDateTime now) {
        return new ReservationSchedule(parkingSpotId, Reservations.none(), true, now);
    }

    public static ReservationSchedule emptyReservationSchedule(LocalDateTime now) {
        return new ReservationSchedule(anyParkingSpotId(), Reservations.none(), true, now);
    }

    public static ReservationSchedule reservationScheduleWith(LocalDateTime now, Reservation reservation) {
        return new ReservationSchedule(anyParkingSpotId(), new Reservations(Set.of(reservation)), true, now);
    }

    public static ReservationSchedule occupiedReservationSchedule(LocalDateTime now) {
        return new ReservationSchedule(anyParkingSpotId(), Reservations.none(), false, now);
    }

}
