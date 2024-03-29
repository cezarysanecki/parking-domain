package pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.application.ReservingWholeParkingSpotRequestHasOccurred;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod;

import java.util.UUID;

@Value
public class ChosenParkingSpotReservationRequested
        implements ClientReservationRequestsEvent, ReservingWholeParkingSpotRequestHasOccurred {

    @NonNull ClientId clientId;
    @NonNull ReservationId reservationId = ReservationId.of(UUID.randomUUID());
    @NonNull ReservationPeriod reservationPeriod;
    @NonNull ParkingSpotId parkingSpotId;

}
