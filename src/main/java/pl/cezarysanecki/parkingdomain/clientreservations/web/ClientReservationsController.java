package pl.cezarysanecki.parkingdomain.clientreservations.web;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.clientreservations.application.CreateReservationRequestCommand;
import pl.cezarysanecki.parkingdomain.clientreservations.application.CreateReservationRequestForChosenParkingSpotCommand;
import pl.cezarysanecki.parkingdomain.clientreservations.application.RequestingReservation;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ClientReservationsController {

    private final RequestingReservation requestingReservation;

    @PostMapping("/client-reservation/{parkingSpotId}")
    ResponseEntity reserveParkingSpot(@PathVariable UUID parkingSpotId, @RequestBody CreateRequestForReservationRequest request) {
        Try<Result> result = requestingReservation.createReservationRequest(new CreateReservationRequestForChosenParkingSpotCommand(
                ClientId.of(request.clientId),
                new ReservationSlot(request.since, request.hours),
                ParkingSpotId.of(parkingSpotId)));

        return result
                .map(success -> switch (success) {
                    case Success -> ResponseEntity.ok().build();
                    case Rejection -> ResponseEntity.badRequest().build();
                })
                .getOrElse(ResponseEntity.internalServerError().build());
    }

    @PostMapping("/client-reservation")
    ResponseEntity reserveAnyParkingSpot(@RequestBody CreateRequestForReservationRequest request) {
        Try<Result> result = requestingReservation.createReservationRequest(new CreateReservationRequestCommand(
                ClientId.of(request.clientId),
                new ReservationSlot(request.since, request.hours)));

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
class CreateRequestForReservationRequest {

    UUID clientId;
    LocalDateTime since;
    int hours;

}
