package pl.cezarysanecki.parkingdomain.reservation.model.events;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod;

@Value
public class ReservationForPartOfParkingSpotMade implements ParkingSpotReservationsEvent {

    @NonNull ReservationId reservationId;
    @NonNull ReservationPeriod reservationPeriod;
    @NonNull ParkingSpotId parkingSpotId;
    @NonNull VehicleSizeUnit vehicleSizeUnit;

}
