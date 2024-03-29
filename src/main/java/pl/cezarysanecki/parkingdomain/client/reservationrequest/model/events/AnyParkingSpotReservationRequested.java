package pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.application.ReservingPartOfParkingSpotRequestHasOccurred;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod;

import java.util.UUID;

@Value
public class AnyParkingSpotReservationRequested
        implements ClientReservationRequestsEvent, ReservingPartOfParkingSpotRequestHasOccurred {

    @NonNull ClientId clientId;
    @NonNull ReservationId reservationId = ReservationId.of(UUID.randomUUID());
    @NonNull ReservationPeriod reservationPeriod;
    @NonNull ParkingSpotType parkingSpotType;
    @NonNull VehicleSizeUnit vehicleSizeUnit;

}
