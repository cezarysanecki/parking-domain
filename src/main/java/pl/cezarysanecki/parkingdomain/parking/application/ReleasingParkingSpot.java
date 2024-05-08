package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.Beneficiary;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvents.ParkingSpotReleased;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;

@Slf4j
@RequiredArgsConstructor
public class ReleasingParkingSpot {

    private final EventPublisher eventPublisher;
    private final BeneficiaryRepository beneficiaryRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    public Try<Occupation> release(OccupationId occupationId) {
        Beneficiary beneficiary = findBeneficiaryBy(occupationId);
        ParkingSpot parkingSpot = findParkingSpotBy(occupationId);

        return parkingSpot.release(occupationId)
                .flatMap(beneficiary::remove)
                .onFailure(exception -> log.error("cannot release parking spot, reason: {}", exception.getMessage()))
                .onSuccess(occupation -> {
                    beneficiaryRepository.save(beneficiary);
                    parkingSpotRepository.save(parkingSpot);

                    eventPublisher.publish(new ParkingSpotReleased(parkingSpot.getParkingSpotId(), occupation));
                });
    }

    private Beneficiary findBeneficiaryBy(OccupationId occupationId) {
        return beneficiaryRepository.findBy(occupationId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find beneficiary containing occupation with id: " + occupationId));
    }

    private ParkingSpot findParkingSpotBy(OccupationId occupationId) {
        return parkingSpotRepository.findBy(occupationId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot containing occupation with id: " + occupationId));
    }

}
