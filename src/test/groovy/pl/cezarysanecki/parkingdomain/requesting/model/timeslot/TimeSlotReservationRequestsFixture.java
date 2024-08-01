package pl.cezarysanecki.parkingdomain.requesting.model.timeslot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requesting.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requesting.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.requesting.ReservationRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeSlotReservationRequestsFixture {

  public static ReservationRequestsTimeSlot timeSlotWithoutRequests() {
    return new ReservationRequestsTimeSlot(
        ReservationRequestsTimeSlotId.newOne(),
        0,
        4,
        Version.zero());
  }

  public static ReservationRequestsTimeSlot timeSlotFullyRequested() {
    return new ReservationRequestsTimeSlot(
        ReservationRequestsTimeSlotId.newOne(),
        4,
        4,
        Version.zero());
  }

  public static ReservationRequestsTimeSlot timeSlotWithRequest(ReservationRequest reservationRequest) {
    return new ReservationRequestsTimeSlot(
        ReservationRequestsTimeSlotId.newOne(),
        4,
        reservationRequest.getSpotUnits().getValue(),
        Version.zero());
  }

}
