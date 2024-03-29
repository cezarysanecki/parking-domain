package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod;

import java.time.LocalDateTime;

@Value
public class CreateReservationRequestForPartOfAnyParkingSpotCommand implements ClientReservationRequestCommand {

    @NonNull ClientId clientId;
    @NonNull ParkingSpotType parkingSpotType;
    @NonNull VehicleSizeUnit vehicleSizeUnit;
    @NonNull ReservationPeriod reservationPeriod;
    @NonNull LocalDateTime when;

}
