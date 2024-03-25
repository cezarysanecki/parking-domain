package pl.cezarysanecki.parkingdomain.reservation.schedule.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod.DayPart.Evening;
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod.DayPart.Morning;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationPeriod {

    Set<DayPart> dayParts;

    public static ReservationPeriod morning() {
        return new ReservationPeriod(Set.of(Morning));
    }

    public static ReservationPeriod evening() {
        return new ReservationPeriod(Set.of(Evening));
    }

    public static ReservationPeriod wholeDay() {
        return new ReservationPeriod(Set.of(Morning, Evening));
    }

    public boolean isPartOf(ReservationPeriod reservationPeriod) {
        return dayParts.stream()
                .anyMatch(reservationPeriod.dayParts::contains);
    }

    public boolean isPartOf(DayPart dayPart) {
        return dayParts.contains(dayPart);
    }

    public enum DayPart {

        Morning, Evening

    }

}
