package pl.cezarysanecki.parkingdomain.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain._local.LocalDateProvider;
import pl.cezarysanecki.parkingdomain.cleaning.policies.CallingCleaningWhenSpotsDirtyPolicy;
import pl.cezarysanecki.parkingdomain.commons.Result;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade;

import java.time.Instant;
import java.util.List;

@Profile("local")
@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
class _LocalController {

  private final LocalDateProvider localDateProvider;
  private final CallingCleaningWhenSpotsDirtyPolicy callingCleaningWhenSpotsDirtyPolicy;
  private final DateProvider dateProvider;
  private final RequestingFacade requestingFacade;

  @PostMapping("/call-cleaning")
  ResponseEntity callCleaning() {
    Result result = callingCleaningWhenSpotsDirtyPolicy.run();
    if (result == Result.Success) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.internalServerError().build();
  }

  @GetMapping("/date")
  Instant getCurrentDate() {
    return localDateProvider.now();
  }

  @PostMapping("/date/pass/{hours}/hours")
  Instant passHours(@PathVariable("hours") int hours) {
    return localDateProvider.passHours(hours);
  }

  @PostMapping("/date/pass/{minutes}/minutes")
  Instant passMinutes(@PathVariable("minutes") int minutes) {
    return localDateProvider.passMinutes(minutes);
  }

  @PostMapping("/requests/make-valid")
  ResponseEntity<List<String>> makeReservationRequestValid() {
    requestingFacade.makeValidFor(dateProvider.currentDay());
    return ResponseEntity.ok().build();
  }

}
