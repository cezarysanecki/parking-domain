package pl.cezarysanecki.parkingdomain.commons.date;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Profile("local")
@RestController
@RequiredArgsConstructor
class LocalDateController {

    private final LocalDateProvider localDateProvider;

    @GetMapping("/date-provider")
    LocalDateTime getCurrentDate() {
        return localDateProvider.now();
    }

    @PostMapping("/date-provider/{hours}/hours")
    LocalDateTime passHours(@PathVariable("hours") int hours) {
        return localDateProvider.passHours(hours);
    }

    @PostMapping("/date-provider/{minutes}/minutes")
    LocalDateTime passMinutes(@PathVariable("minutes") int minutes) {
        return localDateProvider.passMinutes(minutes);
    }

}
