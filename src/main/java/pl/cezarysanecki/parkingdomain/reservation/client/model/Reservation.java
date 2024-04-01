package pl.cezarysanecki.parkingdomain.reservation.client.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;

@Value
public class Reservation {

    @NonNull ClientId clientId;
    @NonNull ReservationId reservationId;
    @NonNull ParkingSpotId parkingSpotId;
    @NonNull VehicleSize vehicleSize;

}
