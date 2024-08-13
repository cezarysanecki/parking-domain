package pl.cezarysanecki.parkingdomain.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parking/view")
@RequiredArgsConstructor
class ParkingViewController {

  private final ViewFreeCurrentParkingSpotsRepository viewFreeCurrentParkingSpotsRepository;

  @GetMapping
  ResponseEntity queryCurrentState() {
    return ResponseEntity.ok(viewFreeCurrentParkingSpotsRepository.query());
  }

}
