package pl.cezarysanecki.parkingdomain._local.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.CreatingReservationRequestTimeSlots;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.MakingReservationRequestsValid;

import java.util.List;

@Profile("local")
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
class LocalReservationRequestsController {

    private final MakingReservationRequestsValid makingReservationRequestsValid;
    private final CreatingReservationRequestTimeSlots creatingReservationRequestTimeSlots;

    @PostMapping("/make-valid")
    ResponseEntity<List<String>> makeReservationRequestValid() {
        io.vavr.collection.List<MakingReservationRequestsValid.Problem> result = makingReservationRequestsValid.makeValid();
        if (result.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.internalServerError()
                .body(result.map(MakingReservationRequestsValid.Problem::reason).asJava());
    }

    @PostMapping("/create-time-slots")
    ResponseEntity createTimeSlots() {
        creatingReservationRequestTimeSlots.prepareNewTimeSlots();
        return ResponseEntity.ok().build();
    }

}
