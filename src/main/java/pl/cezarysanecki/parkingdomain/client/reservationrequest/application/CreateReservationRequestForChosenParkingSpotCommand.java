package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSlot;

@Value
public class CreateReservationRequestForChosenParkingSpotCommand {

    @NonNull ClientId clientId;
    @NonNull ReservationSlot reservationSlot;
    @NonNull ParkingSpotId parkingSpotId;

}
