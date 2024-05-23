package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;

public interface ReservationRequestsTimeSlotRepository {

    void publish(ReservationRequestsTimeSlotEvent event);

    Option<ReservationRequestsTimeSlot> findBy(ReservationRequestsTimeSlotId reservationRequestsTimeSlotId);

    Option<ReservationRequestsTimeSlot> findBy(ReservationRequestId reservationRequestId);

}
