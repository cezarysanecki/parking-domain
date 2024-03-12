package pl.cezarysanecki.parkingdomain.reservation.model;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.GlobalConstants;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationFailed;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationMade;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationCancellationFailed;
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationCancelled;

@RequiredArgsConstructor
public class ReservationSchedule {

    @Getter
    private final ParkingSpotId parkingSpotId;
    private final Reservations reservations;
    private final boolean free;
    private final LocalDateTime now;

    public Either<ReservationFailed, ReservationMade> reserve(Set<Vehicle> vehicles, ReservationSlot reservationSlot) {
        if (isEnoughSpaceFor(vehicles)) {
            return announceFailure(new ReservationFailed(parkingSpotId, "cannot accommodate requested vehicles because of space"));
        }
        if (reservations.intersects(reservationSlot)) {
            return announceFailure(new ReservationFailed(parkingSpotId, "there is another reservation in that time"));
        }
        if (isAlreadyReservedForAny(vehicles)) {
            return announceFailure(new ReservationFailed(parkingSpotId, "it is already reserved for one of vehicles"));
        }
        if (thereIsNoEnoughTimeToFreeSpot(reservationSlot)) {
            return announceFailure(new ReservationFailed(parkingSpotId, "need to give some time to free parking spot"));
        }

        return announceSuccess(new ReservationMade(ReservationId.of(UUID.randomUUID()), parkingSpotId));
    }

    public Either<ReservationCancellationFailed, ReservationCancelled> cancel(ReservationId reservationId) {
        Option<Reservation> reservation = reservations.findBy(reservationId);
        if (reservation.isEmpty()) {
            return announceFailure(new ReservationCancellationFailed(parkingSpotId, "there is no such reservation"));
        }
        long minutesToReservation = reservation.get().minutesTo(now);
        if (minutesToReservation < 60) {
            return announceFailure(new ReservationCancellationFailed(parkingSpotId, "it is too late to cancel reservation"));
        }
        return announceSuccess(new ReservationCancelled(reservationId, parkingSpotId));
    }

    private static boolean isEnoughSpaceFor(final Set<Vehicle> vehicles) {
        Integer requestedSpace = vehicles.stream()
                .map(Vehicle::getVehicleSizeUnit)
                .map(VehicleSizeUnit::getValue)
                .reduce(0, Integer::sum);
        return requestedSpace > GlobalConstants.ParkingSlot.AVAILABLE_SPACE;
    }

    private boolean thereIsNoEnoughTimeToFreeSpot(ReservationSlot reservationSlot) {
        return !free && now.plusHours(2).plusMinutes(59).isAfter(reservationSlot.getSince());
    }

    private boolean isAlreadyReservedForAny(Set<Vehicle> vehicles) {
        return vehicles.stream().map(Vehicle::getVehicleId).anyMatch(reservations::contains);
    }

}
