package pl.cezarysanecki.parkingdomain.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.shared.BusinessDateProvider;
import pl.cezarysanecki.parkingdomain.views.ViewFreeCurrentParkingSpotsRepository;

import java.time.Instant;

@RestController
@RequestMapping("/parking/view")
@RequiredArgsConstructor
class ParkingViewController {

  private final BusinessDateProvider businessDateProvider;
  private final ViewFreeCurrentParkingSpotsRepository viewFreeCurrentParkingSpotsRepository;

  @GetMapping
  ResponseEntity queryCurrentState() {
    Instant activationDate = businessDateProvider.provideDateForActivatingReservations();

    return ResponseEntity.ok(viewFreeCurrentParkingSpotsRepository.queryParkingSpots(activationDate));
  }

}
