package pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastucture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequests;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ParkingSpotRequests map(ParkingSpotRequestsEntity entity) {
        return new ParkingSpotRequests(
                ParkingSpotId.of(entity.parkingSpotId),
                ParkingSpotOccupation.of(
                        entity.requests.stream()
                                .map(vehicleRequestEntity -> vehicleRequestEntity.size)
                                .reduce(0, Integer::sum),
                        entity.capacity),
                entity.requests.stream()
                        .map(vehicleRequestEntity -> vehicleRequestEntity.requestId)
                        .map(RequestId::of)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
