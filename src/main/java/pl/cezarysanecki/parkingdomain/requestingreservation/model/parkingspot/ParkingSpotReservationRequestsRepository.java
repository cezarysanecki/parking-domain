package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import io.vavr.collection.List;
import io.vavr.control.Option;

public interface ParkingSpotReservationRequestsRepository {

    void save(ParkingSpotReservationRequests parkingSpotReservationRequests);

    Option<ParkingSpotReservationRequests> findBy(ParkingSpotTimeSlotId parkingSpotTimeSlotId);

    Option<ParkingSpotReservationRequests> findBy(ReservationRequestId reservationRequestId);

    List<ParkingSpotReservationRequests> findAllWithRequests();

}
