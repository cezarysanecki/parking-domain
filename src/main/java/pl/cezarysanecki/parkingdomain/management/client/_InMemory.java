package pl.cezarysanecki.parkingdomain.management.client;

import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryClientRepository implements ClientRepository {

  private static final Map<ClientId, Client> DATABASE = new ConcurrentHashMap<>();

  @Override
  public void saveNew(Client client) {
    DATABASE.put(client.clientId(), client);
  }

}
