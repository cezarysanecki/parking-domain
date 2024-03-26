package pl.cezarysanecki.parkingdomain.reservation.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

import java.util.Map;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@Value
public class ParkingSpotReservations {

    ParkingSpotId parkingSpotId;
    Map<ReservationPeriod.DayPart, DayPartReservations> dayPartReservations;

    public static ParkingSpotReservations none(ParkingSpotId parkingSpotId) {
        return new ParkingSpotReservations(parkingSpotId, Map.of());
    }

    public Either<ParkingSpotReservationsEvent.ReservationFailed, ParkingSpotReservationsEvent.ReservationForWholeParkingSpotMade> reserveWhole(ReservationId reservationId, ReservationPeriod period) {
        if (isReservationFor(period)) {
            return announceFailure(new ParkingSpotReservationsEvent.ReservationFailed(reservationId, period, parkingSpotId, "there is any reservation for that period"));
        }
        return announceSuccess(new ParkingSpotReservationsEvent.ReservationForWholeParkingSpotMade(reservationId, period, parkingSpotId));
    }

    public Either<ParkingSpotReservationsEvent.ReservationFailed, ParkingSpotReservationsEvent.ReservationForPartOfParkingSpotMade> reservePart(ReservationId reservationId, ReservationPeriod period, VehicleSizeUnit vehicleSizeUnit) {
        if (isAnyIndividualReservationFor(period)) {
            return announceFailure(new ParkingSpotReservationsEvent.ReservationFailed(reservationId, period, parkingSpotId, "there is an individual reservation for that period"));
        }
        return announceSuccess(new ParkingSpotReservationsEvent.ReservationForPartOfParkingSpotMade(reservationId, period, parkingSpotId, vehicleSizeUnit));
    }

    private boolean isReservationFor(ReservationPeriod reservationPeriod) {
        return reservationPeriod.getDayParts()
                .stream()
                .map(dayPart -> dayPartReservations.getOrDefault(dayPart, DayPartReservations.empty(dayPart)))
                .anyMatch(DayPartReservations::isNotEmpty);
    }

    private boolean isAnyIndividualReservationFor(ReservationPeriod reservationPeriod) {
        return reservationPeriod.getDayParts()
                .stream()
                .map(dayPart -> dayPartReservations.getOrDefault(dayPart, DayPartReservations.empty(dayPart)))
                .anyMatch(DayPartReservations::onlyIndividuals);
    }

}
