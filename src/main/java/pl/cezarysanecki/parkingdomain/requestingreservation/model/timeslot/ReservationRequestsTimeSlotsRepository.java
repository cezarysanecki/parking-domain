package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateId;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.Instant;

public interface ReservationRequestsTimeSlotsRepository {

    void saveNewUsing(ReservationRequestsTemplateId reservationRequestsTemplateId, TimeSlot timeSlot);

    void save(ReservationRequestsTimeSlot reservationRequestsTimeSlot);

    Option<ReservationRequestsTimeSlot> findBy(ReservationRequestsTimeSlotId reservationRequestsTimeSlotId);

    Option<ReservationRequestsTimeSlot> findBy(ReservationRequestId reservationRequestId);

    List<ReservationRequestsTimeSlot> findAllValidSince(Instant sinceDate);

    boolean containsAny();

}
