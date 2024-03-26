package pl.cezarysanecki.parkingdomain.reservation.model;

import lombok.Value;

@Value
public class ParkingSpotReservation {

    Reservation reservation;
    ReservationPeriod reservationPeriod;

    public boolean isIndividual() {
        return reservation instanceof Reservation.Individual;
    }

    public boolean isCollective() {
        return reservation instanceof Reservation.Collective;
    }

}
