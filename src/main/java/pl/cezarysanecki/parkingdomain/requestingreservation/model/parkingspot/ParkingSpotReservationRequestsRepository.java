package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

public interface ParkingSpotReservationRequestsRepository {

    void save(ParkingSpotReservationRequests parkingSpotReservationRequests);

    Option<ParkingSpotReservationRequests> findBy(ParkingSpotId parkingSpotId);

    Option<ParkingSpotReservationRequests> findBy(ReservationRequestId reservationRequestId);

}
