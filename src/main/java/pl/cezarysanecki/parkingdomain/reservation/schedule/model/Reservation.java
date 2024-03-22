package pl.cezarysanecki.parkingdomain.reservation.schedule.model;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;

@Value
public class Reservation {

    ReservationId reservationId;
    ClientId clientId;

}
