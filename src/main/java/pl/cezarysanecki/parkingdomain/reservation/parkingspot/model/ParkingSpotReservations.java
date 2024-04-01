package pl.cezarysanecki.parkingdomain.reservation.parkingspot.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ReservationId;

import static pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent.*;

@Value
public class ParkingSpotReservations {

    ParkingSpotCapacity parkingSpotCapacity;

    public Either<ParkingSpotReservationFailed, ParkingSpotReserved> reserve(ReservationId reservationId) {

    }

}
