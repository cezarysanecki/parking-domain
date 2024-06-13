package pl.cezarysanecki.parkingdomain._local.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain._local.config.LocalDateProvider;

import java.time.Instant;

@Profile("local")
@RestController
@RequiredArgsConstructor
class LocalDateController {

  private final LocalDateProvider localDateProvider;

  @GetMapping("/date-provider")
  Instant getCurrentDate() {
    return localDateProvider.now();
  }

  @PostMapping("/date-provider/{hours}/hours")
  Instant passHours(@PathVariable("hours") int hours) {
    return localDateProvider.passHours(hours);
  }

  @PostMapping("/date-provider/{minutes}/minutes")
  Instant passMinutes(@PathVariable("minutes") int minutes) {
    return localDateProvider.passMinutes(minutes);
  }

}
