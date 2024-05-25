package pl.cezarysanecki.parkingdomain._local.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.ExchangingReservationRequestsTimeSlots;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.MakingReservationRequestsValid;

import java.util.List;

@Profile("local")
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
class LocalReservationRequestsController {

  private final MakingReservationRequestsValid makingReservationRequestsValid;
  private final ExchangingReservationRequestsTimeSlots exchangingReservationRequestsTimeSlots;

  @PostMapping("/make-valid")
  ResponseEntity<List<String>> makeReservationRequestValid() {
    makingReservationRequestsValid.makeValidSince();
    return ResponseEntity.ok().build();
  }

  @PostMapping("/create-time-slots")
  ResponseEntity createTimeSlots() {
    exchangingReservationRequestsTimeSlots.exchangeTimeSlots(exchangingReservationRequestsTimeSlots.dateProvider.tomorrow());
    return ResponseEntity.ok().build();
  }

}
