package pl.cezarysanecki.parkingdomain.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber;

@Slf4j
@Component
public class NotificationFacade {

  public void notify(PhoneNumber phoneNumber, String message) {
    log.debug("notifying client with phone number {} with message {}", phoneNumber, message);
  }

}
