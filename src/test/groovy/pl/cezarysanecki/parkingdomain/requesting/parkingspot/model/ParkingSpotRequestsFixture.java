package pl.cezarysanecki.parkingdomain.requesting.parkingspot.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotRequestsFixture {

    public static ParkingSpotRequests parkingSpotWithoutRequests() {
        return new ParkingSpotRequests(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(0, 4), Set.of());
    }

    public static ParkingSpotRequests parkingSpotWithoutPlaceForRequestsWithCapacity(int capacity) {
        return new ParkingSpotRequests(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(0, capacity), Set.of());
    }

    public static ParkingSpotRequests parkingSpotWithoutPlaceForAnyRequests(RequestId requestId) {
        return new ParkingSpotRequests(
                ParkingSpotId.newOne(), ParkingSpotOccupation.of(4, 4), Set.of(requestId));
    }

}
