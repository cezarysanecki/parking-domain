package pl.cezarysanecki.parkingdomain.clientreservationsview.infrastructure;

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

}
