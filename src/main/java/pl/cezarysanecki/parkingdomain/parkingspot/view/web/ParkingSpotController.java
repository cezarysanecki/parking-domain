package pl.cezarysanecki.parkingdomain.parkingspot.view.web;

import io.vavr.control.Try;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.CreatingParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parkingspot.view.model.ParkingSpotView;
import pl.cezarysanecki.parkingdomain.parkingspot.view.model.ParkingSpotViews;

import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
class ParkingSpotController {

    private final CreatingParkingSpot creatingParkingSpot;
    private final ParkingSpotViews parkingSpotViews;

    @GetMapping("/parking-spot/available")
    ResponseEntity<Set<ParkingSpotView>> availableParkingSpots() {
        return ok(parkingSpotViews.queryForAvailableParkingSpots());
    }

    @PostMapping("/parking-spot/create")
    ResponseEntity create(@RequestBody CreateParkingSpotRequest request) {
        Try<Result> result = creatingParkingSpot.create(new CreatingParkingSpot.Command(
                ParkingSpotCapacity.of(request.capacity)
        ));
        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

}

@Data
class CreateParkingSpotRequest {
    Integer capacity;
}

@Data
class OccupyParkingSpotRequest {
    UUID vehicleId;
    Integer size;
}
