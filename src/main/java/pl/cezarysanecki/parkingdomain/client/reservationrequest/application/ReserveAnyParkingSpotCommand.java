package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ReservationType;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

@Value
public class ReserveAnyParkingSpotCommand {

    @NonNull ClientId clientId;
    @NonNull ParkingSpotType parkingSpotType;
    @NonNull VehicleSizeUnit vehicleSizeUnit;
    @NonNull ReservationType reservationType;

}