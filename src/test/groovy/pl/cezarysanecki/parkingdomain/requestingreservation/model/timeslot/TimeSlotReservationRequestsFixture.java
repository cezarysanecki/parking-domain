package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;

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
