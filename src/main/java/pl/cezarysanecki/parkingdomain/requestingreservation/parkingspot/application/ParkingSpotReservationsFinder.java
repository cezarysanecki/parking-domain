package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;

public interface ParkingSpotReservationsFinder {

    Option<ParkingSpotId> findParkingSpotIdBy(ReservationId reservationId);

}
