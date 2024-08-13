package pl.cezarysanecki.parkingdomain.views;

import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;

import java.util.List;

public interface ViewCurrentStateOfClientRepository {

  CurrentStateEntry queryFor(ClientId clientId);

  record CurrentStateEntry(
      ClientId clientId,
      List<OccupationId> occupationIds,
      List<RequestId> requestIds
  ) {
  }

}
