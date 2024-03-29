package pl.cezarysanecki.parkingdomain.views.client.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
class ClientReservationsViewEntity {

    @Id
    Long id;
    UUID clientId;
    Set<ClientReservationViewEntity> reservations;

    ClientReservationsViewEntity(UUID clientId, Set<ClientReservationViewEntity> reservations) {
        this.clientId = clientId;
        this.reservations = reservations;
    }

}
