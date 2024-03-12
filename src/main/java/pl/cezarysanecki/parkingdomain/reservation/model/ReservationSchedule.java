package pl.cezarysanecki.parkingdomain.reservation.model;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationFailed;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationMade;

import java.time.LocalDateTime;
import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@RequiredArgsConstructor
public class ReservationSchedule {

    private final ParkingSpotId parkingSpotId;
    private final Reservations reservations;
    private final boolean free;
    private final LocalDateTime now;



    public Either<ReservationFailed, ReservationMade> reserve(Set<Vehicle> vehicles, ReservationSlot reservationSlot) {
        if (reservations.intersects(reservationSlot)) {
            return announceFailure(new ReservationFailed(parkingSpotId, "there is another reservation in that time"));
        }
        if (isAlreadyReservedForAny(vehicles)) {
            return announceFailure(new ReservationFailed(parkingSpotId, "it is already reserved for one of vehicles"));
        }
        if (thereIsNoEnoughTimeToFreeSpot(reservationSlot)) {
            return announceFailure(new ReservationFailed(parkingSpotId, "need to give some time to free parking spot"));
        }

        return announceSuccess(new ReservationMade(parkingSpotId));
    }

    private boolean thereIsNoEnoughTimeToFreeSpot(ReservationSlot reservationSlot) {
        return !free && now.plusHours(3).isBefore(reservationSlot.getSince());
    }

    private boolean isAlreadyReservedForAny(Set<Vehicle> vehicles) {
        return vehicles.stream().map(Vehicle::getVehicleId).anyMatch(reservations::contains);
    }

}
