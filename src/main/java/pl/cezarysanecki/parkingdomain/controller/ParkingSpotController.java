package pl.cezarysanecki.parkingdomain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.service.ParkingSpotService;

import java.util.List;

@RestController
@RequestMapping("/parking-spot")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    @PostMapping
    public Long create() {
        return parkingSpotService.create().getId();
    }

    @PostMapping("/{id}/occupy")
    public Long occupy(@PathVariable("id") Long id) {
        return parkingSpotService.occupy(id).getId();
    }

    @PostMapping("/{id}/release")
    public Long release(@PathVariable("id") Long id) {
        return parkingSpotService.release(id).getId();
    }

    @GetMapping("/{id}")
    public ParkingSpot findBy(@PathVariable("id") Long id) {
        return parkingSpotService.findBy(id);
    }

    @GetMapping
    public List<ParkingSpot> findAll() {
        return parkingSpotService.findAll();
    }

}
