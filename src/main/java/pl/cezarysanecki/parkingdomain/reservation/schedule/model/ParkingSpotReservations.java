package pl.cezarysanecki.parkingdomain.reservation.schedule.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationForPartOfParkingSpotMade;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationForWholeParkingSpotMade;

import java.util.Map;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationFailed;

@Value
public class ParkingSpotReservations {

    ParkingSpotId parkingSpotId;
    Map<ReservationPeriod.DayPart, DayPartReservations> dayPartReservations;

    public static ParkingSpotReservations none(ParkingSpotId parkingSpotId) {
        return new ParkingSpotReservations(parkingSpotId, Map.of());
    }

    public Either<ReservationFailed, ReservationForWholeParkingSpotMade> reserveWhole(ReservationId reservationId, ReservationPeriod period) {
        if (isReservationFor(period)) {
            return announceFailure(new ReservationFailed(reservationId, period, parkingSpotId, "there is any reservation for that period"));
        }
        return announceSuccess(new ReservationForWholeParkingSpotMade(reservationId, period, parkingSpotId));
    }

    public Either<ReservationFailed, ReservationForPartOfParkingSpotMade> reservePart(ReservationId reservationId, ReservationPeriod period, VehicleSizeUnit vehicleSizeUnit) {
        if (isAnyIndividualReservationFor(period)) {
            return announceFailure(new ReservationFailed(reservationId, period, parkingSpotId, "there is an individual reservation for that period"));
        }
        return announceSuccess(new ReservationForPartOfParkingSpotMade(reservationId, period, parkingSpotId, vehicleSizeUnit));
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
