package pl.cezarysanecki.parkingdomain.reservation.schedule.model;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@RequiredArgsConstructor
public class ReservationSchedule {

    @Getter
    private final ParkingSpotId parkingSpotId;
    private final Reservations reservations;
    private final boolean noOccupation;
    private final LocalDateTime now;

    public ReservationSchedule(ParkingSpotId parkingSpotId, LocalDateTime now) {
        this(parkingSpotId, Reservations.none(), false, now);
    }

    public Either<ReservationScheduleEvent.ReservationFailed, ReservationScheduleEvent.ReservationMade> reserve(
            ClientId clientId,
            ReservationId reservationId
    ) {
        if (isAlreadyReservedFor(clientId)) {
            return announceFailure(new ReservationScheduleEvent.ReservationFailed(parkingSpotId, clientId, reservationId, "it is already reserved for this client"));
        }

        return announceSuccess(new ReservationScheduleEvent.ReservationMade(parkingSpotId, clientId, reservationId));
    }

    public Either<ReservationScheduleEvent.ReservationCancellationFailed, ReservationScheduleEvent.ReservationCancelled> cancel(ReservationId reservationId) {
        Option<Reservation> reservation = reservations.findBy(reservationId);
        if (reservation.isEmpty()) {
            return announceFailure(new ReservationScheduleEvent.ReservationCancellationFailed(
                    parkingSpotId, reservationId, "there is no such reservation"));
        }
        Reservation foundReservation = reservation.get();
        return announceSuccess(new ReservationScheduleEvent.ReservationCancelled(parkingSpotId, foundReservation.getClientId(), reservationId));
    }

    public boolean thereIsReservationFor(ClientId clientId) {
        return isAlreadyReservedFor(clientId);
    }

    public boolean isEmpty() {
        return reservations.isEmpty();
    }

    public Set<ReservationId> findReservationsFor(ClientId clientId) {
        return reservations.getCollection()
                .stream()
                .filter(reservation -> reservation.getClientId().equals(clientId))
                .map(Reservation::getReservationId)
                .collect(Collectors.toUnmodifiableSet());
    }

    private boolean thereIsNoEnoughTimeToFreeSpot(ReservationSlot reservationSlot) {
        return !noOccupation && now.plusHours(2).plusMinutes(59).isAfter(reservationSlot.getSince());
    }

    private boolean isAlreadyReservedFor(ClientId clientId) {
        return reservations.contains(clientId);
    }

}
