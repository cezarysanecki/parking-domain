package pl.cezarysanecki.parkingdomain.views.client.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.cezarysanecki.parkingdomain.views.client.model.ClientReservationStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
class ClientReservationViewEntity {

    UUID reservationId;
    UUID parkingSpotId;
    ClientReservationStatus status;

}
