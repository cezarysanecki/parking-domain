package pl.cezarysanecki.parkingdomain.management.client;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.client.api.BusinessClientRegistered;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.api.IndividualClientRegistered;
import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ClientFacade {

  private final ClientRepository clientRepository;
  private final EventPublisher eventPublisher;

  public Try<ClientId> registerClient(ClientType clientType, PhoneNumber phoneNumber) {
    return Try.of(() -> {
      Client client = new Client(ClientId.newOne(), clientType, phoneNumber);
      log.debug("registering {} client with id {}", clientType.name().toLowerCase(), client.clientId());

      clientRepository.saveNew(client);

      DomainEvent event = switch (clientType) {
        case INDIVIDUAL -> new IndividualClientRegistered(client.clientId());
        case BUSINESS -> new BusinessClientRegistered(client.clientId());
      };
      eventPublisher.publish(event);

      return client.clientId();
    }).onFailure(t -> log.error("failed to register client", t));
  }

}
