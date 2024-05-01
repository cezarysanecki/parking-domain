package pl.cezarysanecki.parkingdomain.requesting.parkingspot.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;

public interface ParkingSpotRequestsRepository {

    ParkingSpotRequests createUsing(ParkingSpotId parkingSpotId, ParkingSpotCapacity parkingSpotCapacity);

    Option<ParkingSpotRequests> findBy(ParkingSpotId parkingSpotId);

    Option<ParkingSpotRequests> findBy(RequestId requestId);

    void publish(ParkingSpotRequestEvent parkingSpotRequestEvent);

}
