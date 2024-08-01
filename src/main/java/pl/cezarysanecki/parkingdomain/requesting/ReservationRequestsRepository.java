package pl.cezarysanecki.parkingdomain.requesting;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.requesting.api.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

public interface ReservationRequestsRepository {

  void publish(ReservationRequestsEvent event);

  Option<ReservationRequests> findBy(RequesterId requesterId, ReservationRequestsTimeSlotId timeSlotId);

  default ReservationRequests getBy(RequesterId requesterId, ReservationRequestsTimeSlotId timeSlotId) {
    return findBy(requesterId, timeSlotId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find reservation requests by requester with id: " + requesterId + " and time slot with id: " + timeSlotId));
  }

  boolean exists(ParkingSpotId parkingSpotId, TimeSlot timeSlot);

  void saveNew(RequestableParkingSpotSectionsGrouped requestableParkingSpotSectionsGrouped);

  RequestableParkingSpotSectionsGrouped findBy(ParkingSpotId parkingSpotId, TimeSlot timeSlot, SpotUnits spotUnits);

  void saveCheckingVersion(RequestableParkingSpotSectionsGrouped requestableParkingSpotSectionsGrouped);

  RequestableParkingSpotSectionsGrouped findBy(ReservationRequestId reservationRequestId);

}
