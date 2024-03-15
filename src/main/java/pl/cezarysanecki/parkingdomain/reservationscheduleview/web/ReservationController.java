package pl.cezarysanecki.parkingdomain.reservationscheduleview.web;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.application.CancelReservationCommand;
import pl.cezarysanecki.parkingdomain.reservationschedule.application.CancellingReservation;
import pl.cezarysanecki.parkingdomain.reservationschedule.application.MakingParkingSlotReservation;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;
import pl.cezarysanecki.parkingdomain.reservationscheduleview.model.ReservationsView;
import pl.cezarysanecki.parkingdomain.reservationscheduleview.model.ReservationsViews;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ReservationController {

    private final ReservationsViews reservationsViews;
    private final MakingParkingSlotReservation makingParkingSlotReservation;
    private final CancellingReservation cancellingReservation;

    @GetMapping("/reservations/{clientId}")
    ResponseEntity<Set<ReservationsView.Reservation>> getReservations(@PathVariable UUID clientId) {
        return ResponseEntity.ok(reservationsViews.findFor(ClientId.of(clientId)).getReservations());
    }

    @PostMapping("/reservation/{parkingSpotId}")
    ResponseEntity reserveParkingSpot(@PathVariable UUID parkingSpotId, @RequestBody MakeReservationRequest request) {
        Try<Result> result = makingParkingSlotReservation.reserve(new ReserveParkingSpotCommand(
                ParkingSpotId.of(parkingSpotId),
                ClientId.of(request.clientId),
                new ReservationSlot(request.since, request.hours)));

        return result
                .map(success -> switch (success) {
                    case Success -> ResponseEntity.ok().build();
                    case Rejection -> ResponseEntity.badRequest().build();
                })
                .getOrElse(ResponseEntity.internalServerError().build());
    }

    @PostMapping("/reservation")
    ResponseEntity reserveAnyParkingSpot(@RequestBody MakeReservationRequest request) {
        Try<Result> result = makingParkingSlotReservation.reserve(new ReserveAnyParkingSpotCommand(
                ClientId.of(request.clientId),
                new ReservationSlot(request.since, request.hours)));

        return result
                .map(success -> switch (success) {
                    case Success -> ResponseEntity.ok().build();
                    case Rejection -> ResponseEntity.badRequest().build();
                })
                .getOrElse(ResponseEntity.internalServerError().build());
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

@Getter
@NoArgsConstructor
@AllArgsConstructor
class MakeReservationRequest {

    UUID clientId;
    LocalDateTime since;
    int hours;

}
