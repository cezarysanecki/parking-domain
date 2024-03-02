package pl.cezarysanecki.parkingdomain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.service.ParkingSpotService;

@RestController
@RequestMapping("/parking-spot")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    @PostMapping
    public Long create() {
        return parkingSpotService.create().getId();
    }

    @GetMapping("/{id}")
    public ParkingSpot findBy(@PathVariable("id") Long id) {
        return parkingSpotService.findBy(id);
    }

}
