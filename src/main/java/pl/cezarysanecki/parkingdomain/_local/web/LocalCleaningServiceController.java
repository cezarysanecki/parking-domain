package pl.cezarysanecki.parkingdomain._local.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.cleaning.application.CallingExternalCleaningServicePolicy;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;

@Profile("local")
@RestController
@RequestMapping("/cleaning")
@RequiredArgsConstructor
class LocalCleaningServiceController {

    private final CallingExternalCleaningServicePolicy callingExternalCleaningServicePolicy;

    @PostMapping("/handle")
    ResponseEntity makeReservationRequestValid() {
        Result result = callingExternalCleaningServicePolicy.handleCleaningPolicy();
        if (result == Result.Success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.internalServerError().build();
    }

}
