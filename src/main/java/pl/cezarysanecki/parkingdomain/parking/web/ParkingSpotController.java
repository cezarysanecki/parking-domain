package pl.cezarysanecki.parkingdomain.parking.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.parking.view.parkingspot.model.ParkingSpotView;
import pl.cezarysanecki.parkingdomain.parking.view.parkingspot.model.ParkingSpotViews;

import java.util.Set;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
class ParkingSpotController {

    private final ParkingSpotViews parkingSpotViews;

    @GetMapping("/parking-spot/available")
    ResponseEntity<Set<ParkingSpotView>> availableParkingSpots() {
        return ok(parkingSpotViews.queryForAvailableParkingSpots());
    }

}
