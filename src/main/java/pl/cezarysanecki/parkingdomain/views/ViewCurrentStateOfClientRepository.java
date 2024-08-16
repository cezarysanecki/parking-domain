package pl.cezarysanecki.parkingdomain.views;

import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;

import java.util.List;
import java.util.UUID;

public interface ViewCurrentStateOfClientRepository {

  List<CurrentStateEntry> queryAll();

  CurrentStateEntry queryFor(ClientId clientId);

  record CurrentStateEntry(
      UUID clientId,
      List<UUID> occupationIds,
      List<UUID> requestIds,
      List<UUID> reservationIds
  ) {
  }

}
