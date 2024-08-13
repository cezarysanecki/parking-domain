package pl.cezarysanecki.parkingdomain.views;

import java.util.List;
import java.util.UUID;

public interface ViewCurrentRequestsRepository {

  List<RequestEntry> queryRequests();

  record RequestEntry(
      UUID requestId,
      UUID requesterId,
      UUID parkingSpotId,
      List<UUID> sectionIds
  ) {
  }

}
