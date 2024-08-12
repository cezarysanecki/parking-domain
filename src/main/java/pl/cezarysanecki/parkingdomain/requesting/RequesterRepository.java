package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;

interface RequesterRepository {

  void saveNew(RequesterId requesterId, int limit);

  Requester findBy(RequesterId requesterId);

}
