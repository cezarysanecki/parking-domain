package pl.cezarysanecki.parkingdomain.requestingreservation.view.client.infrastructure;

import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
class ClientReservationRequestsViewEntity {

    UUID clientId;
    Set<UUID> currentReservationRequests;

}
