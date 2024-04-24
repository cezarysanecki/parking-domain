package pl.cezarysanecki.parkingdomain.requesting.view.client.infrastructure;

import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
class ClientRequestsViewEntity {

    UUID clientId;
    Set<UUID> currentRequests;

}
