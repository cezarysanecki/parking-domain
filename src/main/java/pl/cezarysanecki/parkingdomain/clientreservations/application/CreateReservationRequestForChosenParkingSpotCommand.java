package pl.cezarysanecki.parkingdomain.clientreservations.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

@Value
public class CreateReservationRequestForChosenParkingSpotCommand {

    @NonNull ClientId clientId;
    @NonNull ReservationSlot reservationSlot;
    @NonNull ParkingSpotId parkingSpotId;

}
