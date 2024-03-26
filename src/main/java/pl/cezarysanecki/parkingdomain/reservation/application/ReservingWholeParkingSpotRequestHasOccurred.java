package pl.cezarysanecki.parkingdomain.reservation.application;

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod;

public interface ReservingWholeParkingSpotRequestHasOccurred {

    ReservationId getReservationId();

    ReservationPeriod getReservationPeriod();

    ParkingSpotId getParkingSpotId();

}
