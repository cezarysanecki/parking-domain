package pl.cezarysanecki.parkingdomain.requesting.client.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.client.ClientId;

public interface ClientRequestsRepository {

    Option<ClientRequests> findBy(ClientId clientId);

    Option<ClientRequests> findBy(RequestId requestId);

    ClientRequests publish(ClientRequestsEvent clientRequestsEvent);

}
