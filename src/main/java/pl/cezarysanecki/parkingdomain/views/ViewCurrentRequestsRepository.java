package pl.cezarysanecki.parkingdomain.views;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;

import java.util.List;

public interface ViewCurrentRequestsRepository {

  List<RequestEntry> queryRequests();

  record RequestEntry(
      RequestId requestId,
      RequesterId requesterId,
      ParkingSpotId parkingSpotId,
      List<ParkingSpotSectionId> sectionIds
  ) {
  }

}
