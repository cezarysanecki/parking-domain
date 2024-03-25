package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

@Value
public class CreateReservationRequestForAnyParkingSpotCommand {

    @NonNull ClientId clientId;
    @NonNull ParkingSpotType parkingSpotType;
    @NonNull VehicleSizeUnit vehicleSizeUnit;
    @NonNull ReservationPeriod reservationPeriod;

}
