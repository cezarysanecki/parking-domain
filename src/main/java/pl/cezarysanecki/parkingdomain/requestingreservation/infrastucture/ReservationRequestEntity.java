package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;
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
        ReservationRequesterId.of(requesterId),
        ReservationRequestsTimeSlotId.of(timeSlotId),
        SpotUnits.of(requestedUnits));
  }

}
