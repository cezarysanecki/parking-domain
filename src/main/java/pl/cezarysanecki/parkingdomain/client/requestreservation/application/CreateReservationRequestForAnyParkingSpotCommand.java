package pl.cezarysanecki.parkingdomain.client.requestreservation.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ReservationType;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

@Value
public class CreateReservationRequestForAnyParkingSpotCommand {

    @NonNull ClientId clientId;
    @NonNull ParkingSpotType parkingSpotType;
    @NonNull VehicleSizeUnit vehicleSizeUnit;
    @NonNull ReservationType reservationType;

}
