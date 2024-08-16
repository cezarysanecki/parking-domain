package pl.cezarysanecki.parkingdomain.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.views.ViewCurrentRequestsRepository;
import pl.cezarysanecki.parkingdomain.views.ViewFreeTimeSlotsRepository;

@RestController
@RequestMapping("/requesting/view")
@RequiredArgsConstructor
class RequestingViewController {

  private final ViewCurrentRequestsRepository viewCurrentRequestsRepository;
  private final ViewFreeTimeSlotsRepository viewFreeTimeSlotsRepository;

  @GetMapping
  ResponseEntity queryCurrentState() {
    return ResponseEntity.ok(viewCurrentRequestsRepository.queryRequests());
  }

  @GetMapping("/free")
  ResponseEntity queryFreeTimeSlots() {
    return ResponseEntity.ok(viewFreeTimeSlotsRepository.queryFreeTimeSlots());
  }

}
