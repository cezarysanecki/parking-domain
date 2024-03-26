package pl.cezarysanecki.parkingdomain.reservation.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ReservationEntity {

    @Id
    Long id;
    UUID reservationId;
    Set<ReservationPeriod.DayPart> dayParts;
    Type type;
    Integer vehicleCapacity;

    private ReservationEntity(UUID reservationId, Set<ReservationPeriod.DayPart> dayParts, Type type, Integer vehicleCapacity) {
        this.reservationId = reservationId;
        this.dayParts = dayParts;
        this.type = type;
        this.vehicleCapacity = vehicleCapacity;
    }

    static ReservationEntity individual(UUID reservationId, Set<ReservationPeriod.DayPart> dayParts) {
        return new ReservationEntity(
                reservationId,
                dayParts,
                Type.Individual,
                null);
    }

    static ReservationEntity collective(UUID reservationId, Set<ReservationPeriod.DayPart> dayParts, int vehicleCapacity) {
        return new ReservationEntity(
                reservationId,
                dayParts,
                Type.Collective,
                vehicleCapacity);
    }

    enum Type {
        Individual, Collective
    }

}
