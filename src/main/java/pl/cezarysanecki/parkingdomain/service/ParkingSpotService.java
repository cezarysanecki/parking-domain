package pl.cezarysanecki.parkingdomain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cezarysanecki.parkingdomain.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.model.ParkingSpotStatus;
import pl.cezarysanecki.parkingdomain.model.Vehicle;
import pl.cezarysanecki.parkingdomain.repository.ParkingSpotRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;

    public ParkingSpot create() {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setStatus(ParkingSpotStatus.AVAILABLE);
        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot occupy(Long parkingSpotId, Vehicle vehicle) {
        ParkingSpot parkingSpot = findBy(parkingSpotId);

        parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);
        parkingSpot.setVehicle(vehicle);

        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot occupyAnyAvailable(Vehicle vehicle) {
        ParkingSpot parkingSpot = findAnyAvailable();

        parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);
        parkingSpot.setVehicle(vehicle);

        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot release(Long id) {
        ParkingSpot parkingSpot = findBy(id);

        parkingSpot.setStatus(ParkingSpotStatus.AVAILABLE);

        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot findAnyAvailable() {
        return findAll()
                .stream()
                .filter(parkingSpot -> parkingSpot.getStatus()==ParkingSpotStatus.AVAILABLE)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("cannot find available parking spot"));
    }

    public ParkingSpot findBy(Long id) {
        return parkingSpotRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("cannot find parking spot by id: " + id));
    }

    public List<ParkingSpot> findAll() {
        Iterable<ParkingSpot> parkingSpots = parkingSpotRepository.findAll();
        return StreamSupport.stream(parkingSpots.spliterator(), false)
                .collect(Collectors.toList());
    }

}
