package pl.cezarysanecki.parkingdomain.reservation.model.events;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

@Value
public class ReservationFailed implements ParkingSpotReservationsEvent {

    @NonNull ReservationId reservationId;
    @NonNull ParkingSpotId parkingSpotId;
    @NonNull String reason;

}
