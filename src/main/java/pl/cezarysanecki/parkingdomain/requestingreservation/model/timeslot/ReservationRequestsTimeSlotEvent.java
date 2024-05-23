package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot;

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateId;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

public interface ReservationRequestsTimeSlotEvent {

    ReservationRequestsTimeSlotId timeSlotId();

    record ReservationRequestCreated(
            ReservationRequestsTemplateId templateId,
            ReservationRequestsTimeSlotId timeSlotId,
            TimeSlot timeSlot
    ) implements ReservationRequestsTimeSlotEvent {
    }

    record ReservationRequestAppended(
            ReservationRequestsTimeSlotId timeSlotId,
            ReservationRequest reservationRequest,
            Version timeSlotVersion
    ) implements ReservationRequestsTimeSlotEvent {
    }

    record ReservationRequestRemoved(
            ReservationRequestsTimeSlotId timeSlotId,
            ReservationRequest reservationRequest,
            Version timeSlotVersion
    ) implements ReservationRequestsTimeSlotEvent {
    }

}
