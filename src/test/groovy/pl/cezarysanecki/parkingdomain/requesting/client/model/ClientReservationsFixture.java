package pl.cezarysanecki.parkingdomain.requesting.client.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientReservationsFixture {

    public static ClientRequests clientWithNoRequests() {
        return new ClientRequests(ClientId.newOne(), Set.of());
    }

    public static ClientRequests clientWithRequest(RequestId requestId) {
        return new ClientRequests(ClientId.newOne(), Set.of(requestId));
    }

}
