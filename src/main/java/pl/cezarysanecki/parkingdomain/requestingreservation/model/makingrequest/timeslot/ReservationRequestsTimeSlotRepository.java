package pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateId;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.Instant;

public interface ReservationRequestsTimeSlotRepository {

  void saveNew(ReservationRequestsTimeSlotId timeSlotId, ReservationRequestsTemplateId templateId, TimeSlot timeSlot);

  Option<ReservationRequestsTimeSlot> findBy(ReservationRequestsTimeSlotId reservationRequestsTimeSlotId);

  boolean containsAny();

  void removeAllValidSince(Instant date);

}
