package pl.cezarysanecki.parkingdomain.clientreservationsview.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
class ClientReservationViewEntity {

    UUID reservationId;
    UUID parkingSpotId;
    LocalDateTime since;
    LocalDateTime until;

}
