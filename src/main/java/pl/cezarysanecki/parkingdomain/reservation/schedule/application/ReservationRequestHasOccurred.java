package pl.cezarysanecki.parkingdomain.reservation.schedule.application;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSlot;

public interface ReservationRequestHasOccurred {

    ClientId getClientId();

    ReservationId getReservationId();

    ReservationSlot getReservationSlot();

    Option<ParkingSpotId> getParkingSpotId();

}
