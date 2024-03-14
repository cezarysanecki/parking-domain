package pl.cezarysanecki.parkingdomain.reservation.application;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.reservation.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationSlot;

@Value
public class ReserveAnyParkingSpotCommand {

    ClientId clientId;
    ReservationSlot reservationSlot;

}
