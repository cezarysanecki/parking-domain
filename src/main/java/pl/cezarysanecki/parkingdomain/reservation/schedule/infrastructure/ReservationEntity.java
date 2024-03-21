package pl.cezarysanecki.parkingdomain.reservation.schedule.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ReservationEntity {

    @Id
    Long id;
    UUID reservationId;
    LocalDateTime since;
    LocalDateTime until;
    UUID clientId;

    ReservationEntity(UUID reservationId, LocalDateTime since, LocalDateTime until, UUID clientId) {
        this.reservationId = reservationId;
        this.since = since;
        this.until = until;
        this.clientId = clientId;
    }

}
