package pl.cezarysanecki.parkingdomain.reservation.application;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationSlot;

import java.util.Set;

@Value
public class CancelReservationCommand {

    ReservationId reservationId;

}
