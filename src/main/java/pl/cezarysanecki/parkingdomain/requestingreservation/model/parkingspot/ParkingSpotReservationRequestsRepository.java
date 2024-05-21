package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.Instant;

public interface ParkingSpotReservationRequestsRepository {

    void store(NewParkingSpotReservationRequests newOne);

    void publish(ParkingSpotReservationRequestsEvents event);

    Option<ParkingSpotReservationRequests> findBy(ParkingSpotTimeSlotId parkingSpotTimeSlotId);

    Option<ParkingSpotReservationRequests> findBy(ParkingSpotCategory parkingSpotCategory, TimeSlot timeSlot);

    Option<ParkingSpotReservationRequests> findBy(ReservationRequestId reservationRequestId);

    List<ParkingSpotReservationRequests> findAllRequestsValidFrom(Instant sinceDate);

    record NewParkingSpotReservationRequests(
            ParkingSpotId parkingSpotId,
            ParkingSpotTimeSlotId parkingSpotTimeSlotId,
            ParkingSpotCategory parkingSpotCategory,
            ParkingSpotCapacity capacity,
            TimeSlot timeSlot) {

    }

}
