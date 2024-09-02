package pl.cezarysanecki.parkingdomain.management.client;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import static pl.cezarysanecki.parkingdomain.jooq.tables.ClientCatalogue.CLIENT_CATALOGUE;

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdClientRepository implements ClientRepository {

  private final DSLContext create;

  @Override
  public void saveNew(Client client) {
    create.insertInto(CLIENT_CATALOGUE)
        .set(CLIENT_CATALOGUE.ID, client.clientId().value())
        .set(CLIENT_CATALOGUE.TYPE, client.type().name())
        .set(CLIENT_CATALOGUE.PHONE_NUMBER, client.phoneNumber().getValue())
        .execute();
  }

}
