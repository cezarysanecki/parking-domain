package pl.cezarysanecki.parkingdomain.reservation.schedule.application;

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod;

public interface ReservingWholeParkingSpotRequestHasOccurred {

    ReservationId getReservationId();

    ReservationPeriod getReservationPeriod();

    ParkingSpotId getParkingSpotId();

}
