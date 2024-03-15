package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

@Value
public class ParkingSpotReservation {

    @NonNull ClientId clientId;
    @NonNull ReservationId reservationId;

}
