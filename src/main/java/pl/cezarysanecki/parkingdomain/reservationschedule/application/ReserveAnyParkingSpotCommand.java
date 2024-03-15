package pl.cezarysanecki.parkingdomain.reservationschedule.application;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

@Value
public class ReserveAnyParkingSpotCommand {

    ClientId clientId;
    ReservationSlot reservationSlot;

}
