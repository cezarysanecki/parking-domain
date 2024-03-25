package pl.cezarysanecki.parkingdomain.views.client.web;

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
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.CreateReservationRequestForAnyParkingSpotCommand;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.CreateReservationRequestForChosenParkingSpotCommand;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.CreatingReservationRequest;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod;
import pl.cezarysanecki.parkingdomain.views.client.model.ClientReservationsView;
import pl.cezarysanecki.parkingdomain.views.client.model.ClientsReservationsViews;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ClientReservationsViewController {

    private final ClientsReservationsViews clientsReservationsViews;
    private final CreatingReservationRequest creatingReservationRequest;
    private final CancellingReservation cancellingReservation;

    @PostMapping("/client-reservation/{parkingSpotId}")
    ResponseEntity reserveParkingSpot(@PathVariable UUID parkingSpotId, @RequestBody CreateRequestForReservationRequest request) {
        Try<Result> result = creatingReservationRequest.createReservationRequest(new CreateReservationRequestForChosenParkingSpotCommand(
                ClientId.of(request.clientId),
                ParkingSpotId.of(parkingSpotId),
                ReservationPeriod.Whole));

        return result
                .map(success -> switch (success) {
                    case Success -> ResponseEntity.ok().build();
                    case Rejection -> ResponseEntity.badRequest().build();
                })
                .getOrElse(() -> ResponseEntity.internalServerError().build());
    }

    @PostMapping("/client-reservation")
    ResponseEntity reserveAnyParkingSpot(@RequestBody CreateRequestForReservationRequest request) {
        Try<Result> result = creatingReservationRequest.createReservationRequest(new CreateReservationRequestForAnyParkingSpotCommand(
                ClientId.of(request.clientId),
                ParkingSpotType.Gold,
                null, null));

        return result
                .map(success -> switch (success) {
                    case Success -> ResponseEntity.ok().build();
                    case Rejection -> ResponseEntity.badRequest().build();
                })
                .getOrElse(() -> ResponseEntity.internalServerError().build());
    }

    @DeleteMapping("/client-reservation/{reservationId}")
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

    @GetMapping("/client-reservation/{clientId}")
    ResponseEntity<Set<ClientReservationsView.Reservation>> getReservationsForClient(@PathVariable UUID clientId) {
        return ResponseEntity.ok(
                clientsReservationsViews.findFor(ClientId.of(clientId))
                        .getReservations());
    }

}

@Getter
@NoArgsConstructor
@AllArgsConstructor
class CreateRequestForReservationRequest {

    UUID clientId;
    LocalDateTime since;
    int hours;

}
