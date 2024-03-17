package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

@Value
public class ParkReservedVehicleCommand {

    @NonNull ReservationId reservationId;
    @NonNull Vehicle vehicle;

}
