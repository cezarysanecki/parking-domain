package pl.cezarysanecki.parkingdomain.reserving.parkingspot.application;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId;

public interface ParkingSpotReservationsFinder {

    Option<ParkingSpotId> findParkingSpotIdBy(ReservationId reservationId);

}
