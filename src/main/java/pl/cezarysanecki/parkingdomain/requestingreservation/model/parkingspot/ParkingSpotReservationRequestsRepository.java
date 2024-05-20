package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

public interface ParkingSpotReservationRequestsRepository {

    void store(NewParkingSpotReservationRequests newOne);

    void save(ParkingSpotReservationRequests parkingSpotReservationRequests);

    Option<ParkingSpotReservationRequests> findBy(ParkingSpotTimeSlotId parkingSpotTimeSlotId);

    Option<ParkingSpotReservationRequests> findBy(ReservationRequestId reservationRequestId);

    List<ParkingSpotReservationRequests> findAllWithRequests();

    record NewParkingSpotReservationRequests(
            ParkingSpotId parkingSpotId,
            ParkingSpotTimeSlotId parkingSpotTimeSlotId,
            ParkingSpotCapacity capacity,
            TimeSlot timeSlot) {

    }

}
