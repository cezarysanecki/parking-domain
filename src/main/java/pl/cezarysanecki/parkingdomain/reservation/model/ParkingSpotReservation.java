package pl.cezarysanecki.parkingdomain.reservation.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParkingSpotReservation {

    @Getter
    private final ReservationPeriod reservationPeriod;
    private final Reservation reservation;

    public boolean isIndividual() {
        return reservation instanceof Reservation.Individual;
    }

    public boolean isCollective() {
        return reservation instanceof Reservation.Collective;
    }

}
