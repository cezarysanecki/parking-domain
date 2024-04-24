package pl.cezarysanecki.parkingdomain.requesting.view.client.model;

import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class ClientRequestsView {

    UUID clientId;
    Set<UUID> currentRequests;

}
