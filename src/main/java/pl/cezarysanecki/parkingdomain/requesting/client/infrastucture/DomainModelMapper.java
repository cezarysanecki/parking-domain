package pl.cezarysanecki.parkingdomain.requesting.client.infrastucture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequests;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ClientRequests map(ClientRequestsEntity entity) {
        return new ClientRequests(
                ClientId.of(entity.clientId),
                entity.requests.stream()
                        .map(RequestId::of)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
