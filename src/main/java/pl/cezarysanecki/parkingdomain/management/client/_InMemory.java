package pl.cezarysanecki.parkingdomain.management.client;

import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;

import java.util.Map;

import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.CLIENT_DATABASE;

class InMemoryClientRepository implements ClientRepository {

  private static final Map<ClientId, Client> DATABASE = CLIENT_DATABASE;

  @Override
  public void saveNew(Client client) {
    if (DATABASE.values()
        .stream()
        .map(Client::phoneNumber)
        .anyMatch(phoneNumber -> phoneNumber.equals(client.phoneNumber()))) {
      throw new IllegalStateException("Phone number already in use");
    }
    DATABASE.put(client.clientId(), client);
  }

}
