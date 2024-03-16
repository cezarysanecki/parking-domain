package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@AllArgsConstructor
class ParkingSpotReservationEntity {

    @Id
    Long id;
    UUID clientId;
    UUID reservationId;

    ParkingSpotReservationEntity(UUID clientId, UUID reservationId) {
        this.clientId = clientId;
        this.reservationId = reservationId;
    }

}
