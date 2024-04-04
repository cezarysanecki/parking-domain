package pl.cezarysanecki.parkingdomain.reservation.view.client.web;

import io.vavr.control.Try;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.reservation.client.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.reservation.client.application.SubmittingReservationRequestForPartOfParkingSpot;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.view.client.model.ClientReservationsView;
import pl.cezarysanecki.parkingdomain.reservation.view.client.model.ClientReservationsViews;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
class ClientReservationsController {

    private final ClientReservationsViews clientReservationsViews;
    private final SubmittingReservationRequestForPartOfParkingSpot submittingReservationRequestForPartOfParkingSpot;
    private final CancellingReservationRequest cancellingReservationRequest;

    @GetMapping("/client-reservations/{clientId}")
    ResponseEntity<ClientReservationsView> getClientReservations(@PathVariable UUID clientId) {
        ClientReservationsView clientReservationsView = clientReservationsViews.getClientReservationsViewFor(ClientId.of(clientId));
        return ResponseEntity.ok(clientReservationsView);
    }

    @PostMapping("/client-reservations/{clientId}")
    ResponseEntity create(@PathVariable UUID clientId, @RequestBody SubmitReservationRequest request) {
        Try<Result> result = submittingReservationRequestForPartOfParkingSpot.requestReservation(
                new SubmittingReservationRequestForPartOfParkingSpot.Command(
                        ClientId.of(clientId),
                        ParkingSpotId.of(request.parkingSpotId),
                        VehicleSize.of(request.vehicleSize)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/client-reservations/reservation/{reservationId}")
    ResponseEntity cancel(@PathVariable UUID reservationId) {
        Try<Result> result = cancellingReservationRequest.cancelReservationRequest(
                new CancellingReservationRequest.Command(ReservationId.of(reservationId)));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

}

@Data
class SubmitReservationRequest {
    UUID parkingSpotId;
    Integer vehicleSize;
}
