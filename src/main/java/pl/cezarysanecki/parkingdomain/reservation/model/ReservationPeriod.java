package pl.cezarysanecki.parkingdomain.reservation.model;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod.DayPart.Evening;
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod.DayPart.Morning;

@Value
@RequiredArgsConstructor
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

    public enum DayPart {

        Morning, Evening

    }

}
