package pl.cezarysanecki.parkingdomain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.model.ParkingSpotStatus;
import pl.cezarysanecki.parkingdomain.model.Vehicle;
import pl.cezarysanecki.parkingdomain.repository.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.repository.VehicleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public ParkingSpot create() {
        return parkingSpotRepository.save(new ParkingSpot());
    }

    @Transactional
    public ParkingSpot reservedAnyFor(Vehicle vehicle) {
        ParkingSpot parkingSpot = findAnyAvailable();

        parkingSpot.reserveFor(vehicle);

        return parkingSpotRepository.save(parkingSpot);
    }

    @Transactional
    public ParkingSpot reservedFor(Long parkingSpotId, Vehicle vehicle) {
        ParkingSpot parkingSpot = parkingSpotRepository.findBy(parkingSpotId);

        parkingSpot.reserveFor(vehicle);

        return parkingSpotRepository.save(parkingSpot);
    }

    @Transactional
    public ParkingSpot release(Long id) {
        ParkingSpot parkingSpot = parkingSpotRepository.findBy(id);

        parkingSpot.setStatus(ParkingSpotStatus.AVAILABLE);

        parkingSpot.getVehicles().forEach(vehicle -> vehicle.setParkingSpot(null));
        vehicleRepository.saveAll(parkingSpot.getVehicles());

        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot deleteReservationOn(Long parkingSpotId) {
        ParkingSpot parkingSpot = parkingSpotRepository.findBy(parkingSpotId);
        parkingSpot.deleteReservation();
        return parkingSpotRepository.save(parkingSpot);
    }

    @Transactional(readOnly = true)
    public ParkingSpot findAnyAvailable() {
        return parkingSpotRepository.findAll()
                .stream()
                .filter(ParkingSpot::isAvailable)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("cannot find available parking spot"));
    }

    @Transactional(readOnly = true)
    public ParkingSpot findAnyAvailableFor(Vehicle vehicle) {
        List<ParkingSpot> parkingSpots = parkingSpotRepository.findAll();

        return parkingSpots.stream()
                .filter(parkingSpot -> !parkingSpot.isFull() && parkingSpot.isTheSameType(vehicle.getType()))
                .findAny()
                .or(() -> parkingSpots.stream()
                        .filter(ParkingSpot::isAvailable)
                        .findAny())
                .orElseThrow(() -> new IllegalArgumentException("cannot find available parking spot"));
    }

}
