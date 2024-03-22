package pl.cezarysanecki.parkingdomain.reservation.schedule.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ReservationEntity {

    @Id
    Long id;
    UUID reservationId;
    UUID clientId;

    ReservationEntity(UUID reservationId, UUID clientId) {
        this.reservationId = reservationId;
        this.clientId = clientId;
    }

}
