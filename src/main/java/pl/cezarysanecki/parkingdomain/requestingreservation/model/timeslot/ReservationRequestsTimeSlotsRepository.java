package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequestId;

public interface ReservationRequestsTimeSlotsRepository {

    void publish(ReservationRequestsTimeSlotEvent event);

    Option<ReservationRequestsTimeSlot> findBy(ReservationRequestsTimeSlotId reservationRequestsTimeSlotId);

    Option<ReservationRequestsTimeSlot> findBy(ReservationRequestId reservationRequestId);

}
