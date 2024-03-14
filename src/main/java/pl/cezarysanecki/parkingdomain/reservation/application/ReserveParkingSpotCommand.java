package pl.cezarysanecki.parkingdomain.reservation.application;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationSlot;

@Value
public class ReserveParkingSpotCommand {

    ParkingSpotId parkingSpotId;
    ClientId clientId;
    ReservationSlot reservationSlot;

}
