package pl.cezarysanecki.parkingdomain.reservation.schedule.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class DayPartReservations {

    @Getter
    private final ReservationPeriod.DayPart dayPart;
    private final Set<Reservation> collection;

    public static DayPartReservations empty(ReservationPeriod.DayPart dayPart) {
        return new DayPartReservations(dayPart, Set.of());
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public boolean onlyIndividuals() {
        return collection.stream().allMatch(Reservation.Individual.class::isInstance);
    }

    public boolean areMixed() {
        return !collection.isEmpty() && !onlyCollectives();
    }

    public boolean onlyCollectives() {
        return collection.stream().allMatch(Reservation.Collective.class::isInstance);
    }

}
