package pl.cezarysanecki.parkingdomain._local.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.ExchangingReservationRequestsTimeSlots;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.MakingReservationRequestsValid;

import java.time.Duration;
import java.util.List;

@Profile("local")
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
class LocalReservationRequestsController {

  private final DateProvider dateProvider;
  private final MakingReservationRequestsValid makingReservationRequestsValid;
  private final ExchangingReservationRequestsTimeSlots exchangingReservationRequestsTimeSlots;

  @PostMapping("/make-valid")
  ResponseEntity<List<String>> makeReservationRequestValid() {
    makingReservationRequestsValid.makeAllValidSince(dateProvider.now().plus(Duration.ofHours(1)));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/create-time-slots")
  ResponseEntity createTimeSlots() {
    exchangingReservationRequestsTimeSlots.exchangeTimeSlots(dateProvider.tomorrowMidnight());
    return ResponseEntity.ok().build();
  }

}
