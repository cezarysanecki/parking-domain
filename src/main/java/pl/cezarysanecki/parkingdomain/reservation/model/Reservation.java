package pl.cezarysanecki.parkingdomain.reservation.model;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;

import java.util.Set;

@Value
public class Reservation {

    ParkingSpotId parkingSpotId;
    ReservationSlot reservationSlot;
    Set<Vehicle> vehicles;

    public boolean intersects(ReservationSlot slot) {
        return reservationSlot.intersects(slot);
    }

}
