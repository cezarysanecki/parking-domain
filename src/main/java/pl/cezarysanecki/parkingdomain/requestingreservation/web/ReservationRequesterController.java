package pl.cezarysanecki.parkingdomain.requestingreservation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static pl.cezarysanecki.parkingdomain.requestingreservation.web.ReservationRequesterViewRepository.ReservationRequesterView;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
class ReservationRequesterController {

    private final ReservationRequesterViewRepository reservationRequesterViewRepository;

    @GetMapping("/requesters")
    ResponseEntity<List<ReservationRequesterView>> requesters() {
        return ok(reservationRequesterViewRepository.queryForAllReservationRequesters());
    }

}
