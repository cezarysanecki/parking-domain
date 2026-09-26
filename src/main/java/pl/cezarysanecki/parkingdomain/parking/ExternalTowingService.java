package pl.cezarysanecki.parkingdomain.parking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;

import java.util.List;

public interface ExternalTowingService {

  void call(List<OccupationId> occupationsToTow);

  @Slf4j
  @Component
  class DummyExternalTowingService implements ExternalTowingService {

    @Override
    public void call(List<OccupationId> occupationsToTow) {
      log.debug("imitating to call external towing service for {} vehicles (could be outbox pattern)", occupationsToTow.size());
    }

  }

}
