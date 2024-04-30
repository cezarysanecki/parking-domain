package pl.cezarysanecki.parkingdomain.catalogue.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

interface CatalogueClientDatabase {

    void saveNew(Client client);

    class InMemoryCatalogueClientDatabase implements CatalogueClientDatabase {

        private static final Map<ClientId, ClientDatabaseRow> DATABASE = new ConcurrentHashMap<>();

        @Override
        public void saveNew(Client client) {
            DATABASE.put(
                    client.getClientId(),
                    new ClientDatabaseRow(
                            client.getClientId().getValue(),
                            client.getPhoneNumber().getValue()));
        }

    }

}

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ClientDatabaseRow {

    UUID clientId;
    String phoneNumber;

    Client toClient() {
        return new Client(clientId, phoneNumber);
    }

}
