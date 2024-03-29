package pl.cezarysanecki.parkingdomain.client.view.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.cezarysanecki.parkingdomain.client.view.model.ClientReservationStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
class ClientReservationViewEntity {

    UUID reservationId;
    UUID parkingSpotId;
    ClientReservationStatus status;

}
