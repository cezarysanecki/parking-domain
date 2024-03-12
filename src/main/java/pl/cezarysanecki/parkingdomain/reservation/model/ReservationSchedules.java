package pl.cezarysanecki.parkingdomain.reservation.model;

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

public interface ReservationSchedules {

    ReservationSchedule findBy(ParkingSpotId parkingSpotId);

    ReservationSchedule findFreeFor(ReservationSlot reservationSlot);

    ReservationSchedule publish(ReservationEvent event);

}
