package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;

import java.util.List;

record Requester(
    RequesterId requesterId,
    List<RequestId> requests,
    int limit,
    Version version
) {

  public Requester {
    if (requests.size() > limit) {
      throw new IllegalStateException("current usage cannot exceed limit");
    }
    if (limit == 0) {
      throw new IllegalStateException("value of limit must be positive");
    }
  }

  boolean canAppend(RequestId requestId) {
    return !willBeTooManyRequests(requestId);
  }

  private boolean willBeTooManyRequests(RequestId requestId) {
    return requests.size() + 1 > limit;
  }

}
