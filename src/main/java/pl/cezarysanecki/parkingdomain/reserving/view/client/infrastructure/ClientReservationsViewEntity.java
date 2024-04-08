package pl.cezarysanecki.parkingdomain.reserving.view.client.infrastructure;

import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
class ClientReservationsViewEntity {

    UUID clientId;
    Set<UUID> currentReservations;

}
