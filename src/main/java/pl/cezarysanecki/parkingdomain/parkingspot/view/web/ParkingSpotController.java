package pl.cezarysanecki.parkingdomain.parkingspot.view.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.parkingspot.view.model.ParkingSpotView;
import pl.cezarysanecki.parkingdomain.parkingspot.view.model.ParkingSpotViews;

import java.util.Set;
import java.util.UUID;

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

@Data
class OccupyParkingSpotRequest {
    UUID vehicleId;
    Integer size;
}
