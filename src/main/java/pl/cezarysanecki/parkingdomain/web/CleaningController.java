package pl.cezarysanecki.parkingdomain.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.cleaning.CleaningFacade;
import pl.cezarysanecki.parkingdomain.commons.Result;

@RestController
@RequestMapping("/cleaning")
@RequiredArgsConstructor
class CleaningController {

  private final CleaningFacade cleaningFacade;

  @PostMapping("/mark-as-done")
  ResponseEntity markAsDone() {
    Result result = cleaningFacade.markCleaningAsDone();
    return result == Result.Success ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();
  }

}
