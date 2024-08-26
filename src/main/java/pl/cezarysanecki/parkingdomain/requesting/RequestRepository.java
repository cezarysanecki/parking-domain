package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;

import java.time.LocalDate;
import java.util.List;

interface RequestRepository {

  void saveCheckingVersion(Request request);

  boolean delete(RequestId requestId);

  List<RequestForReservation> findAllBy(LocalDate day);

  void deleteAll(List<RequestId> requestIds);

}
