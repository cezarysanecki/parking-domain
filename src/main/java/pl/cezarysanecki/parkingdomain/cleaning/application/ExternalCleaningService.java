package pl.cezarysanecki.parkingdomain.cleaning.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

public interface ExternalCleaningService {

  void call();

  @Slf4j
  @Component
  class DummyExternalCleaningService implements ExternalCleaningService {

    @Override
    public void call() {
      log.debug("Imitating to call external cleaning service (could be outbox pattern)");
    }

  }

}
