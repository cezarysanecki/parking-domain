package pl.cezarysanecki.parkingdomain.requestingreservation.web;

import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.util.List;
import java.util.UUID;

public interface ReservationRequesterViewRepository {

  List<ReservationRequesterView> queryForAllReservationRequesters();

  record ReservationRequesterView(
      UUID requesterId,
      List<ReservationRequestView> reservationRequests
  ) {

    public record ReservationRequestView(
        UUID reservationRequestId,
        UUID parkingSpotId,
        TimeSlot timeSlot
    ) {
    }

  }

}
