package pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model.ParkingSpotRequestsView;
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model.ParkingSpotRequestsViews;

import java.util.Set;

@RestController
@RequiredArgsConstructor
class ParkingSpotRequestsController {

    private final ParkingSpotRequestsViews parkingSpotRequestsViews;

    @GetMapping("/parking-spot-requests")
    ResponseEntity<Set<ParkingSpotRequestsView>> getAll() {
        Set<ParkingSpotRequestsView> allParkingSpotRequests = parkingSpotRequestsViews.getAllParkingSpots();
        return ResponseEntity.ok(allParkingSpotRequests);
    }

    @GetMapping("/parking-spot-requests/available")
    ResponseEntity<Set<ParkingSpotRequestsView>> getAllAvailable() {
        Set<ParkingSpotRequestsView> allParkingSpotRequests = parkingSpotRequestsViews.getAvailableParkingSpots();
        return ResponseEntity.ok(allParkingSpotRequests);
    }

}
