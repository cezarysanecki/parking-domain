package pl.cezarysanecki.parkingdomain.reservationschedule.application;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

public interface ReservationRequestHasOccurred {

    ClientId getClientId();

    ReservationId getReservationId();

    ReservationSlot getReservationSlot();

    Option<ParkingSpotId> getParkingSpotId();

}
