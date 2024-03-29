package pl.cezarysanecki.parkingdomain.reservation.model;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

import pl.cezarysanecki.parkingdomain.reservation.model.events.ReservationFailed;

import pl.cezarysanecki.parkingdomain.reservation.model.events.ReservationForPartOfParkingSpotMade;

import pl.cezarysanecki.parkingdomain.reservation.model.events.ReservationForWholeParkingSpotMade;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotReservations {

    ParkingSpotId parkingSpotId;
    Map<ReservationPeriod.DayPart, Set<ParkingSpotReservation>> dayPartReservations;

    public static ParkingSpotReservations none(ParkingSpotId parkingSpotId) {
        return new ParkingSpotReservations(parkingSpotId, Map.of());
    }

    public static ParkingSpotReservations of(ParkingSpotId parkingSpotId, Set<ParkingSpotReservation> reservations) {
        Map<ReservationPeriod.DayPart, Set<ParkingSpotReservation>> dayPartReservations = reservations.stream()
                .map(reservation -> reservation.getReservationPeriod()
                        .getDayParts()
                        .stream()
                        .collect(Collectors.toMap(dayPart -> dayPart, dayPart -> reservation)))
                .flatMap(map -> map.entrySet().stream())
                .collect(
                        HashMap::new,
                        (map, entry) -> map.computeIfAbsent(entry.getKey(), key -> new HashSet<>()).add(entry.getValue()),
                        HashMap::putAll);

        return new ParkingSpotReservations(parkingSpotId, dayPartReservations);
    }

    public Either<ReservationFailed, ReservationForWholeParkingSpotMade> reserveWhole(ReservationId reservationId, ReservationPeriod period) {
        if (isReservationFor(period)) {
            return announceFailure(new ReservationFailed(reservationId, "there is any reservation for that period"));
        }
        return announceSuccess(new ReservationForWholeParkingSpotMade(reservationId, period, parkingSpotId));
    }

    public Either<ReservationFailed, ReservationForPartOfParkingSpotMade> reservePart(ReservationId reservationId, ReservationPeriod period, VehicleSizeUnit vehicleSizeUnit) {
        if (isAnyIndividualReservationFor(period)) {
            return announceFailure(new ReservationFailed(reservationId, "there is an individual reservation for that period"));
        }
        return announceSuccess(new ReservationForPartOfParkingSpotMade(reservationId, period, parkingSpotId, vehicleSizeUnit));
    }

    public boolean isEmpty() {
        return dayPartReservations.isEmpty();
    }

    public boolean contains(ReservationId reservationId) {
        return dayPartReservations.values()
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(reservation -> reservation.getReservation().getReservationId().equals(reservationId));
    }

    private boolean isReservationFor(ReservationPeriod reservationPeriod) {
        return reservationPeriod.getDayParts()
                .stream()
                .map(dayPart -> dayPartReservations.getOrDefault(dayPart, Set.of()))
                .anyMatch(not(Set::isEmpty));
    }

    private boolean isAnyIndividualReservationFor(ReservationPeriod reservationPeriod) {
        return reservationPeriod.getDayParts()
                .stream()
                .map(dayPart -> dayPartReservations.getOrDefault(dayPart, Set.of()))
                .flatMap(Collection::stream)
                .anyMatch(ParkingSpotReservation::isIndividual);
    }

}
