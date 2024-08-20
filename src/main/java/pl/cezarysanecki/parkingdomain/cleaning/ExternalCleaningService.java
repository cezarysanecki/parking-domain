package pl.cezarysanecki.parkingdomain.cleaning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

public interface ExternalCleaningService {

  void call();

  @Slf4j
  @Component
  class DummyExternalCleaningService implements ExternalCleaningService {

    @Override
    public void call() {
      log.debug("imitating to call external cleaning service (could be outbox pattern)");
    }

  }

}
