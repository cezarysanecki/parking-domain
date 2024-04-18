package pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model.ParkingSpotReservationsView;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model.ParkingSpotReservationsViews;

import java.util.Set;

@RestController
@RequiredArgsConstructor
class ParkingSpotReservationsController {

    private final ParkingSpotReservationsViews parkingSpotReservationsViews;

    @GetMapping("/parking-spot-reservations")
    ResponseEntity<Set<ParkingSpotReservationsView>> getAll() {
        Set<ParkingSpotReservationsView> allParkingSpotReservations = parkingSpotReservationsViews.getAllParkingSpots();
        return ResponseEntity.ok(allParkingSpotReservations);
    }

    @GetMapping("/parking-spot-reservations/available")
    ResponseEntity<Set<ParkingSpotReservationsView>> getAllAvailable() {
        Set<ParkingSpotReservationsView> allParkingSpotReservations = parkingSpotReservationsViews.getAvailableParkingSpots();
        return ResponseEntity.ok(allParkingSpotReservations);
    }

}
