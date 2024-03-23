package pl.cezarysanecki.parkingdomain.client.requestreservation.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ReservationType;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

@Value
public class CreateReservationRequestForChosenParkingSpotCommand {

    @NonNull ClientId clientId;
    @NonNull ParkingSpotId parkingSpotId;
    @NonNull ReservationType reservationType;

}
