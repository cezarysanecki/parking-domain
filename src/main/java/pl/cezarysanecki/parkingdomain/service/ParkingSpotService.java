package pl.cezarysanecki.parkingdomain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public ParkingSpot create() {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setStatus(ParkingSpotStatus.AVAILABLE);
        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot reservedAnyFor(Vehicle vehicle) {
        ParkingSpot parkingSpot = findAnyAvailable();

        if (parkingSpot.getReservedBy() != null && !parkingSpot.getReservedBy().getId().equals(vehicle.getId())) {
            throw new IllegalStateException("cannot reserve reserved parking spot");
        }
        if (parkingSpot.getStatus() != ParkingSpotStatus.AVAILABLE) {
            throw new IllegalStateException("cannot reserve unavailable parking spot");
        }

        parkingSpot.setStatus(ParkingSpotStatus.RESERVED);
        parkingSpot.setReservedBy(vehicle);

        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot reservedFor(Long parkingSpotId, Vehicle vehicle) {
        ParkingSpot parkingSpot = parkingSpotRepository.findBy(parkingSpotId);

        if (parkingSpot.getReservedBy() != null && !parkingSpot.getReservedBy().getId().equals(vehicle.getId())) {
            throw new IllegalStateException("cannot reserve reserved parking spot");
        }
        if (parkingSpot.getStatus() != ParkingSpotStatus.AVAILABLE) {
            throw new IllegalStateException("cannot reserve unavailable parking spot");
        }

        parkingSpot.setStatus(ParkingSpotStatus.RESERVED);
        parkingSpot.setReservedBy(vehicle);

        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot release(Long id) {
        ParkingSpot parkingSpot = parkingSpotRepository.findBy(id);

        parkingSpot.setStatus(ParkingSpotStatus.AVAILABLE);

        parkingSpot.getVehicles().forEach(vehicle -> vehicle.setParkingSpot(null));
        vehicleRepository.saveAll(parkingSpot.getVehicles());

        return parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot findAnyAvailable() {
        return parkingSpotRepository.findAll()
                .stream()
                .filter(ParkingSpot::isAvailable)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("cannot find available parking spot"));
    }

    public ParkingSpot findAnyAvailableFor(Vehicle vehicle) {
        List<ParkingSpot> parkingSpots = parkingSpotRepository.findAll();

        return parkingSpots.stream()
                .filter(parkingSpot -> !parkingSpot.isFull() && vehicle.isTheSameType(parkingSpot))
                .findAny()
                .or(() -> parkingSpots.stream()
                        .filter(ParkingSpot::isAvailable)
                        .findAny())
                .orElseThrow(() -> new IllegalArgumentException("cannot find available parking spot"));
    }

    public ParkingSpot deleteReservationOn(Long parkingSpotId) {
        ParkingSpot parkingSpot = parkingSpotRepository.findBy(parkingSpotId);
        parkingSpot.setReservedBy(null);
        if (parkingSpot.getVehicles().isEmpty()) {
            parkingSpot.setStatus(ParkingSpotStatus.AVAILABLE);
        } else {
            parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);
        }
        return parkingSpotRepository.save(parkingSpot);
    }

}
