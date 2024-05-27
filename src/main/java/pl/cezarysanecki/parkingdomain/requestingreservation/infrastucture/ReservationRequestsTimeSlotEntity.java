package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
class ReservationRequestsTimeSlotEntity {

  UUID timeSlotId;
  int capacity;
  Instant from;
  Instant to;
  UUID templateId;
  int version;

  ReservationRequestsTimeSlot toDomain(List<ReservationRequestEntity> reservationRequests) {
    return new ReservationRequestsTimeSlot(
        ReservationRequestsTimeSlotId.of(timeSlotId),
        reservationRequests.stream()
            .map(reservationRequest -> reservationRequest.requestedUnits)
            .reduce(0, Integer::sum),
        capacity,
        new Version(version));
  }

}
