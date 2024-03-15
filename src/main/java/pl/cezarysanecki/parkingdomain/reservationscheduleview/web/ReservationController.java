package pl.cezarysanecki.parkingdomain.reservationscheduleview.web;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.reservationschedule.application.CancelReservationCommand;
import pl.cezarysanecki.parkingdomain.reservationschedule.application.CancellingReservation;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservationscheduleview.model.ReservationsView;
import pl.cezarysanecki.parkingdomain.reservationscheduleview.model.ReservationsViews;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ReservationController {

    private final ReservationsViews reservationsViews;
    private final CancellingReservation cancellingReservation;

    @GetMapping("/reservations/{clientId}")
    ResponseEntity<Set<ReservationsView.Reservation>> getReservationsForClient(@PathVariable UUID clientId) {
        return ResponseEntity.ok(
                reservationsViews.findFor(ClientId.of(clientId))
                        .getReservations());
    }

    @DeleteMapping("/reservation/{reservationId}")
    ResponseEntity cancelReservation(@PathVariable UUID reservationId) {
        Try<Result> result = cancellingReservation.cancel(new CancelReservationCommand(
                ReservationId.of(reservationId)));

        return result
                .map(success -> switch (success) {
                    case Success -> ResponseEntity.ok().build();
                    case Rejection -> ResponseEntity.badRequest().build();
                })
                .getOrElse(ResponseEntity.internalServerError().build());
    }

}
