package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

@Value
public class CreateReservationRequestForChosenParkingSpotCommand {

    @NonNull ClientId clientId;
    @NonNull ParkingSpotId parkingSpotId;
    @NonNull ReservationPeriod reservationPeriod;

}
