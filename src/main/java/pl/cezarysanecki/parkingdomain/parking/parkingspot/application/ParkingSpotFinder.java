package pl.cezarysanecki.parkingdomain.parking.parkingspot.application;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId;

public interface ParkingSpotFinder {

    Option<ParkingSpotId> findBy(ReservationId reservationId);

}
