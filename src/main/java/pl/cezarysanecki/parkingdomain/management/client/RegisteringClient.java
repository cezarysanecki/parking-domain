package pl.cezarysanecki.parkingdomain.management.client;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RegisteringClient {

    private final CatalogueClientDatabase database;
    private final EventPublisher eventPublisher;

    public Try<Result> registerClient(ClientType clientType, String phoneNumber) {
        return Try.<Result>of(() -> {
            Client client = new Client(UUID.randomUUID(), clientType, phoneNumber);
            log.debug("registering client with id {}", client.getClientId());

            database.saveNew(client);

            eventPublisher.publish(new ClientRegistered(client));

            return new Result.Success<>(client.getClientId());
        }).onFailure(t -> log.error("failed to register client", t));
    }

}
