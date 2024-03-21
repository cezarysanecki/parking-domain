package pl.cezarysanecki.parkingdomain.reservation.schedule.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

public interface ReservationSchedules {

    ReservationSchedule createFor(ParkingSpotId parkingSpotId);

    ReservationSchedule markOccupation(ParkingSpotId parkingSpotId, boolean occupied);

    Option<ReservationSchedule> findBy(ParkingSpotId parkingSpotId);

    Option<ReservationSchedule> findBy(ReservationId reservationId);

    Option<ReservationSchedule> findFreeFor(ReservationSlot reservationSlot);

    ReservationSchedule publish(ReservationScheduleEvent event);

}
