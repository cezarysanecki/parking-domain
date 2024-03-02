package pl.cezarysanecki.parkingdomain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cezarysanecki.parkingdomain.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.model.ParkingSpotStatus;
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

}
