package pl.cezarysanecki.parkingdomain.fee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotForceReleased;

@Slf4j
@Component
class FeeEventHandler {

  @Transactional
  @EventListener(value = ParkingSpotForceReleased.class, condition = "#event.reason() == T(pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotForceReleased$Reason).NOT_RELEASED_PARKING_SPOT")
  public void handle(ParkingSpotForceReleased event) {
    log.debug("Fee for {} client is {}$", event.occupantId(), "50.00");
  }

}
