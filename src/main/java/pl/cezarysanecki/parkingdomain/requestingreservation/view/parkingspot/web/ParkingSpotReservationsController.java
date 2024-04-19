package pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model.ParkingSpotReservationRequestsView;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model.ParkingSpotReservationRequestsViews;

import java.util.Set;

@RestController
@RequiredArgsConstructor
class ParkingSpotReservationsController {

    private final ParkingSpotReservationRequestsViews parkingSpotReservationRequestsViews;

    @GetMapping("/parking-spot-reservation-requests")
    ResponseEntity<Set<ParkingSpotReservationRequestsView>> getAll() {
        Set<ParkingSpotReservationRequestsView> allParkingSpotReservations = parkingSpotReservationRequestsViews.getAllParkingSpots();
        return ResponseEntity.ok(allParkingSpotReservations);
    }

    @GetMapping("/parking-spot-reservation-requests/available")
    ResponseEntity<Set<ParkingSpotReservationRequestsView>> getAllAvailable() {
        Set<ParkingSpotReservationRequestsView> allParkingSpotReservations = parkingSpotReservationRequestsViews.getAvailableParkingSpots();
        return ResponseEntity.ok(allParkingSpotReservations);
    }

}
