package pl.cezarysanecki.parkingdomain.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;

@Slf4j
@Component
public class NotificationFacade {

  public void notify(ClientId clientId, String message) {
    log.debug("notifying client with id {} with message {}", clientId, message);
  }

}
