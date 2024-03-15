package pl.cezarysanecki.parkingdomain.reservationschedule.application;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

@Value
public class ReserveParkingSpotCommand {

    ParkingSpotId parkingSpotId;
    ClientId clientId;
    ReservationSlot reservationSlot;

}
