package pl.cezarysanecki.parkingdomain.reservation.parkingspot.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;

public interface ParkingSpotReservationsRepository {

    Option<ParkingSpotReservations> findBy(ParkingSpotId parkingSpotId);

    ParkingSpotReservations publish(ParkingSpotReservationEvent parkingSpotReservationEvent);

}
