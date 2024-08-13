package pl.cezarysanecki.parkingdomain._local.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requesting.ExchangingReservationRequestsTimeSlots;
import pl.cezarysanecki.parkingdomain.requesting.MakingReservationRequestsValid;
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade;

import java.time.Duration;
import java.util.List;

@Profile("local")
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
class LocalReservationRequestsController {

  private final DateProvider dateProvider;
  private final RequestingFacade requestingFacade;

  @PostMapping("/make-valid")
  ResponseEntity<List<String>> makeReservationRequestValid() {
    requestingFacade.makeValidFor(dateProvider.now());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/create-time-slots")
  ResponseEntity createTimeSlots() {
    exchangingReservationRequestsTimeSlots.exchangeTimeSlots(dateProvider.tomorrowMidnight());
    return ResponseEntity.ok().build();
  }

}
