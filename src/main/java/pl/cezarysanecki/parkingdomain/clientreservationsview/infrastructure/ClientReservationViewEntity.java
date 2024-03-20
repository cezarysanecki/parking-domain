package pl.cezarysanecki.parkingdomain.clientreservationsview.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
class ClientReservationViewEntity {

    UUID reservationId;
    UUID parkingSpotId;
    ClientReservationStatus status;
    LocalDateTime since;
    LocalDateTime until;

}
