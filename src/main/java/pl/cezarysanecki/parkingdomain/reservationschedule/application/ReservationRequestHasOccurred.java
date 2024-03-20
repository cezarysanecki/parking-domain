package pl.cezarysanecki.parkingdomain.reservationschedule.application;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

import java.util.UUID;

public interface ReservationRequestHasOccurred {

    ClientId getClientId();

    default ReservationId getReservationId() {
        return ReservationId.of(UUID.randomUUID());
    }

    ReservationSlot getReservationSlot();

    Option<ParkingSpotId> getParkingSpotId();

}
