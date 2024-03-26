package pl.cezarysanecki.parkingdomain.parking.application.parking;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

@Value
public class ParkReservedVehicleCommand {

    @NonNull ReservationId reservationId;
    @NonNull Vehicle vehicle;

}
