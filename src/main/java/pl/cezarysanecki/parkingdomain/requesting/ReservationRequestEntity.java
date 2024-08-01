package pl.cezarysanecki.parkingdomain.requesting;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.UUID;

@AllArgsConstructor
class ReservationRequestEntity {

  UUID reservationRequestId;
  UUID requesterId;
  UUID timeSlotId;
  int requestedUnits;

  ReservationRequest toDomain() {
    return new ReservationRequest(
        ReservationRequestId.of(reservationRequestId),
        RequesterId.of(requesterId),
        ReservationRequestsTimeSlotId.of(timeSlotId),
        SpotUnits.of(requestedUnits));
  }

}
