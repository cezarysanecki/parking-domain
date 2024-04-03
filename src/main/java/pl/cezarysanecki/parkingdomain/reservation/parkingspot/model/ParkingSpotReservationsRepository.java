package pl.cezarysanecki.parkingdomain.reservation.parkingspot.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;

public interface ParkingSpotReservationsRepository {

    ParkingSpotReservations createUsing(ParkingSpotId parkingSpotId, ParkingSpotCapacity parkingSpotCapacity);

    Option<ParkingSpotReservations> findBy(ParkingSpotId parkingSpotId);

    void publish(ParkingSpotReservationEvent parkingSpotReservationEvent);

}
