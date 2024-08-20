package pl.cezarysanecki.parkingdomain.management.client;

import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;

interface ClientRepository {

  void saveNew(Client client);

  Client findBy(ClientId clientId);

}
