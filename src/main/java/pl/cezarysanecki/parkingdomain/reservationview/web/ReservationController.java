package pl.cezarysanecki.parkingdomain.reservationview.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.reservation.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservationview.model.ReservationsView;
import pl.cezarysanecki.parkingdomain.reservationview.model.ReservationsViews;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ReservationController {

    private final ReservationsViews reservationsViews;

    @GetMapping("/reservations/{clientId}")
    ResponseEntity<Set<ReservationsView.Reservation>> getReservations(@PathVariable UUID clientId) {
        return ResponseEntity.ok(reservationsViews.findFor(ClientId.of(clientId)).getReservations());
    }

}
