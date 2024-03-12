package pl.cezarysanecki.parkingdomain.reservation.model;

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

public interface ReservationSchedules {

    ReservationSchedule findBy(ParkingSpotId parkingSpotId);

    ReservationSchedule publish(ReservationEvent event);

}
