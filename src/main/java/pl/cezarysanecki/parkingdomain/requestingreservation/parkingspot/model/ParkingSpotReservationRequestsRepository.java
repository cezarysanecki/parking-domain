package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;

public interface ParkingSpotReservationRequestsRepository {

    ParkingSpotReservationRequests createUsing(ParkingSpotId parkingSpotId, ParkingSpotCapacity parkingSpotCapacity);

    Option<ParkingSpotReservationRequests> findBy(ParkingSpotId parkingSpotId);

    Option<ParkingSpotReservationRequests> findBy(ReservationId reservationId);

    void publish(ParkingSpotReservationRequestEvent parkingSpotReservationRequestEvent);

}
