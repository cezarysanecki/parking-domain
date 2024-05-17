package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;

@Slf4j
@RequiredArgsConstructor
public class ProvidingUsageOfParkingSpot {

    private final ParkingSpotRepository parkingSpotRepository;

    public void makeOutOfUse(ParkingSpotId parkingSpotId) {
        ParkingSpot parkingSpot = findParkingSpotBy(parkingSpotId);
        log.debug("found parking spot with id {}", parkingSpot.getParkingSpotId());

        parkingSpot.makeOutOfUse();
        log.debug("parking spot with id {} is out of use", parkingSpot.getParkingSpotId());

        parkingSpotRepository.save(parkingSpot);
    }

    public void putIntoService(ParkingSpotId parkingSpotId) {
        ParkingSpot parkingSpot = findParkingSpotBy(parkingSpotId);
        log.debug("found parking spot with id {}", parkingSpot.getParkingSpotId());

        parkingSpot.putIntoService();
        log.debug("parking spot with id {} is put into service", parkingSpot.getParkingSpotId());

        parkingSpotRepository.save(parkingSpot);
    }

    private ParkingSpot findParkingSpotBy(ParkingSpotId parkingSpotId) {
        return parkingSpotRepository.findBy(parkingSpotId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id: " + parkingSpotId));
    }

}
