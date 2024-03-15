package pl.cezarysanecki.parkingdomain.reservationsscheduleview.web;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.reservationschedule.application.CancelReservationCommand;
import pl.cezarysanecki.parkingdomain.reservationschedule.application.CancellingReservation;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ReservationsScheduleViewController {

    private final CancellingReservation cancellingReservation;

    @DeleteMapping("/reservation/{reservationId}")
    ResponseEntity cancelReservation(@PathVariable UUID reservationId) {
        Try<Result> result = cancellingReservation.cancel(new CancelReservationCommand(
                ReservationId.of(reservationId)));

        return result
                .map(success -> switch (success) {
                    case Success -> ResponseEntity.ok().build();
                    case Rejection -> ResponseEntity.badRequest().build();
                })
                .getOrElse(() -> ResponseEntity.internalServerError().build());
    }

}
