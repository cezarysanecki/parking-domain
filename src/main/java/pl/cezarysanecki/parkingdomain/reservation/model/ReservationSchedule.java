package pl.cezarysanecki.parkingdomain.reservation.model;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationFailed;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationMade;

import java.time.LocalDateTime;
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
    private final boolean noOccupation;
    private final LocalDateTime now;

    public Either<ReservationFailed, ReservationMade> reserve(ClientId clientId, ReservationSlot reservationSlot) {
        if (reservations.intersects(reservationSlot)) {
            return announceFailure(new ReservationFailed(parkingSpotId, "there is another reservation in that time"));
        }
        if (isAlreadyReservedFor(clientId)) {
            return announceFailure(new ReservationFailed(parkingSpotId, "it is already reserved for one of vehicles"));
        }
        if (thereIsNoEnoughTimeToFreeSpot(reservationSlot)) {
            return announceFailure(new ReservationFailed(parkingSpotId, "need to give some time to free parking spot"));
        }

        return announceSuccess(new ReservationMade(ReservationId.of(UUID.randomUUID()), parkingSpotId, reservationSlot, clientId));
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

    public boolean thereIsReservationFor(ClientId clientId) {
        return isAlreadyReservedFor(clientId);
    }

    public boolean isEmpty() {
        return reservations.isEmpty();
    }

    private boolean thereIsNoEnoughTimeToFreeSpot(ReservationSlot reservationSlot) {
        return !noOccupation && now.plusHours(2).plusMinutes(59).isAfter(reservationSlot.getSince());
    }

    private boolean isAlreadyReservedFor(ClientId clientId) {
        return reservations.contains(clientId);
    }

}
