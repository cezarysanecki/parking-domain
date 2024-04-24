package pl.cezarysanecki.parkingdomain.parking.parkingspot.application;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId;

public interface FindingParkingSpotReservations {

    Option<ParkingSpotId> findParkingSpotIdByAssigned(ReservationId reservationId);

}
