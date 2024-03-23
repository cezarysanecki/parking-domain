package pl.cezarysanecki.parkingdomain.reservation.schedule.application;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

public interface ReservationRequestHasOccurred {

    ClientId getClientId();

    ReservationId getReservationId();

    Option<ParkingSpotId> getParkingSpotId();

}
