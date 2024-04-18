package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;

public interface ParkingSpotReservationsRepository {

    ParkingSpotReservations createUsing(ParkingSpotId parkingSpotId, ParkingSpotCapacity parkingSpotCapacity);

    Option<ParkingSpotReservations> findBy(ParkingSpotId parkingSpotId);

    Option<ParkingSpotReservations> findBy(ReservationId reservationId);

    void publish(ParkingSpotReservationEvent parkingSpotReservationEvent);

}
