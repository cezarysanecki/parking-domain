package pl.cezarysanecki.parkingdomain.reservationschedule.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

public interface ReservationSchedules {

    Option<ReservationSchedule> findBy(ParkingSpotId parkingSpotId);

    Option<ReservationSchedule> findBy(ReservationId reservationId);

    Option<ReservationSchedule> findFreeFor(ReservationSlot reservationSlot);

    ReservationSchedule publish(ReservationScheduleEvent event);

}