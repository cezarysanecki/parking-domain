package pl.cezarysanecki.parkingdomain.management.client;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientRegistered;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ClientFacade {

  private final ClientRepository clientRepository;
  private final EventPublisher eventPublisher;

  public Optional<ClientId> registerClient(ClientType clientType, PhoneNumber phoneNumber) {
    try {
      Client client = new Client(ClientId.newOne(), clientType, phoneNumber);
      log.debug("registering {} client with id {}", clientType.name().toLowerCase(), client.clientId());

      clientRepository.saveNew(client);

      ClientRegistered event = switch (clientType) {
        case INDIVIDUAL -> new ClientRegistered.IndividualClient(client.clientId());
        case BUSINESS -> new ClientRegistered.BusinessClient(client.clientId());
      };
      eventPublisher.publish(event);

      return Optional.of(client.clientId());
    } catch (Exception exception) {
      log.error("failed to register client", exception);
      return Optional.empty();
    }
  }

}
