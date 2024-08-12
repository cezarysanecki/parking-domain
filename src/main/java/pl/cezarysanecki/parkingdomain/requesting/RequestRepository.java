package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;

import java.time.Instant;
import java.util.List;

interface RequestRepository {

  void saveCheckingVersion(Request request);

  boolean delete(RequestId requestId);

  List<Request> findAllBy(Instant date);

  void deleteAll(List<RequestId> requestIds);

}
