package pl.cezarysanecki.parkingdomain.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cleaning/view")
@RequiredArgsConstructor
class CleaningViewController {

  private final ViewCleaningRepository viewCleaningRepository;

  @GetMapping
  ResponseEntity queryCurrentState() {
    return ResponseEntity.ok(viewCleaningRepository.query());
  }

}
